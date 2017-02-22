package net.majorkernelpanic.streaming;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.SeekBar;

import net.majorkernelpanic.streaming.rtp.RtpSocket;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * adjust bitrate dynamically. Respond automatically to changing network conditions
 * If socket fifo free% is less than #BAD_THRESHOLD % full, reduce bitrate.
 * If socket fifo free% is more than #GOOD_THRESHOLD % full, increase bitrate.
 * Netflix claims 1050 kbps produces 640x480 without artifacts for most video
 * Polling rate assumes a fifo size of 300 and should be tweaked if this number is changed.
 * <p>
 * http://techblog.netflix.com/2015/12/per-title-encode-optimization.html
 * Created by scottbaar on 10/30/15.
 */
public class BitrateController {
    public static int DEFAULT_BITRATE = 500000;
    public static int MIN_BITRATE = 50000;
    public static int MAX_BITRATE = 1050000;//per bitrate ladder
    private int currentBitrate;
    final MediaCodec videoCodec;
    boolean enabled;
    RtpSocket rtpSocket;

    private static final float GOOD_THRESHOLD = .98f;
    private static final float BAD_THRESHOLD = .70f;
    private static final int adjustBitrateMaxTime = 2000;//adjust the bitrate no more often than this time;ms

    private Handler handler = new Handler(Looper.getMainLooper());
    private static final int seekbarMultiplier = 1000;
    SeekBar seekBar;
    private static final String TAG = "BitrateController";

    public BitrateController(MediaCodec videoCodec, int currentBitrate, RtpSocket socket, SeekBar bitrateSB) {
        this.videoCodec = videoCodec;
        this.currentBitrate = currentBitrate;
        this.rtpSocket = socket;
        this.seekBar = bitrateSB;
        enabled = Build.VERSION.SDK_INT >= 19 && videoCodec != null;

        bitrateSB.setMax((MAX_BITRATE - MIN_BITRATE) / seekbarMultiplier);
        bitrateSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                forceBitrate((seekBar.getProgress() * seekbarMultiplier) + MIN_BITRATE);
            }
        });
        socket.setBitrateController(this);
        bitrateSB.setProgress(currentBitrate / seekbarMultiplier);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                thread.start();
            }
        }, 7000);//wait until the fifo can start to fill.
    }

    public void forceBitrate(int newBitrate) {
        if (!enabled) return;
        setBitrate(newBitrate);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setBitrate(int newBitrate) {
        if (!enabled) return;
        int adjustedBitrate = Math.max(MIN_BITRATE, Math.min(MAX_BITRATE, newBitrate));
        if (adjustedBitrate != newBitrate) {
            newBitrate = adjustedBitrate;
        }
        if (currentBitrate == newBitrate) return;
        Bundle params = new Bundle();
        params.putInt(MediaCodec.PARAMETER_KEY_VIDEO_BITRATE, newBitrate);
        try {
            videoCodec.setParameters(params);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        currentBitrate = newBitrate;
       /* if (BuildConfig.DEBUG){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,"Setting new bitrate "+(currentBitrate/1000)+ "k",Toast.LENGTH_SHORT).show();
                }
            });
        }*/
        lastTimeBitrateAdjusted = System.currentTimeMillis();
        seekBar.setProgress(currentBitrate / seekbarMultiplier);

    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (enabled) {
                    //readStats();
                    int availablePackets = rtpSocket.getBufferRequestedPermitsAndReset();
                    float freePercent = percent(availablePackets);
                    if (queueDumped) {//there was a blockage, report full.
                        freePercent = 0;
                        queueDumped = false;
                    }
                    //Debug.e("bitrate "+ rtpSocket.getBufferIn() + " "+ rtpSocket.getBufferOut() + " "+ availablePackets + " " + freePercent);
                    onNewSocketInfo(freePercent);
                    Thread.sleep(3000 + (freePercent == 0 ? 4000 : 0));//add extra time if the buffer is being filled from scratch
                }
            } catch (Exception e) {
                Log.e(TAG, "error in bitrate controller " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    });

    long lastTimeBitrateAdjusted;//ms

    private void onNewSocketInfo(float freePercent) {
        if (System.currentTimeMillis() < (lastTimeBitrateAdjusted + adjustBitrateMaxTime)) return;
        if (freePercent >= GOOD_THRESHOLD) {
            setBitrate(currentBitrate + getBitrateStep());
        } else if (freePercent <= BAD_THRESHOLD) {
            setBitrate(currentBitrate - getBitrateStep() * 2);
        }

    }

    //gets the amount bitrate should go up or down.
    private int getBitrateStep() {
        return currentBitrate / 8;
    }

    private float percent(int numerator) {
        return (numerator * 1f) / rtpSocket.getBufferCount();
    }

    public void release() {
        enabled = false;
    }

    boolean queueDumped = false;

    public void onQueueDumped() {
        queueDumped = true;
        //requestSyncFrame();

    }

    //seems to make playback errors a lot more common.
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void requestSyncFrame() {
        if (!enabled) return;
        Bundle params = new Bundle();
        params.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
        videoCodec.setParameters(params);
    }

    String mPath = "/proc/net/udp";

    public void readStats() {
        try {
            FileInputStream fis = new FileInputStream(mPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String s;
            //while ((s = bufferedReader.readLine())!=null)
            //  Debug.d("proc net","proc net " +s);
            bufferedReader.close();
        } catch (Exception e) {
            //Debug.e("proc net error "+ e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}