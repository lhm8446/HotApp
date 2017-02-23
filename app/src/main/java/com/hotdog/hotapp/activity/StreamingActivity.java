package com.hotdog.hotapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Camera.CameraInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.vo.UserVo;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;
import net.majorkernelpanic.streaming.video.VideoQuality;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamingActivity extends Activity implements
        OnClickListener,
        RtspClient.Callback,
        Session.Callback,
        SurfaceHolder.Callback,
        OnCheckedChangeListener {

    public final static String TAG = "StreamingActivity";
    private MediaPlayer mp;
    private ImageButton mButtonVideo, mButtonStart, mButtonCamera, lightoff;
    private RadioGroup mRadioGroup;
    private FrameLayout mLayoutVideoSettings;
    private SurfaceView mSurfaceView;
    private TextView mTextBitrate;
    private ProgressBar mProgressBar;
    private static Session mSession;
    private RtspClient mClient;
    private UserVo userVo;
    private int secPass;
    private String nickname;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor editor;
    private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

            if (key.equals("stream")) {
                if ("check".equals(prefs.getString(key, ""))) {
                    if (mClient.isStreaming()) {
                        editor.putString("stream", "true");
                        editor.apply();
                    } else {
                        editor.putString("stream", "false");
                        editor.apply();
                    }
                }
            } else if (key.equals("flag")) {
                String flag = prefs.getString(key, "");
                if ("stop".equals(flag)) {
                    if (mClient.isStreaming()) {
                        toggleStream();
                        editor.putString("stream", "false");
                        editor.apply();
                        onDestroy();
                        finish();
                    } else {
                        editor.putString("stream", "false");
                        editor.apply();
                        onDestroy();
                        finish();
                    }
                } else if ("camera".equals(flag)) {
                    Toast.makeText(StreamingActivity.this, "camera", Toast.LENGTH_SHORT).show();
                    //switchCam();
                } else if ("high".equals(flag)) {
                    mSession.setVideoQuality(new VideoQuality(640, 480, 30, 900000));
                    if (mClient.isStreaming()) {
                        toggleStream();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                toggleStream();
                            }
                        }).start();
                    } else {
                        toggleStream();
                    }
                } else if ("middle".equals(flag)) {
                    mSession.setVideoQuality(new VideoQuality(352, 288, 30, 600000));
                    if (mClient.isStreaming()) {
                        toggleStream();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                toggleStream();
                            }
                        }).start();
                    } else {
                        toggleStream();
                    }
                } else if ("low".equals(flag)) {
                    mSession.setVideoQuality(new VideoQuality(176, 144, 30, 500000));
                    if (mClient.isStreaming()) {
                        toggleStream();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                toggleStream();
                            }
                        }).start();
                    } else {
                        toggleStream();
                    }
                } else if ("audio".equals(flag)) {
                    try {
                        mp3Player(mPrefs.getString("audio", ""));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_streaming);
        mButtonVideo = (ImageButton) findViewById(R.id.videosettings);
        mButtonStart = (ImageButton) findViewById(R.id.start);
        mButtonCamera = (ImageButton) findViewById(R.id.camera);
        lightoff = (ImageButton) findViewById(R.id.lightoff);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mTextBitrate = (TextView) findViewById(R.id.bitrate);
        mLayoutVideoSettings = (FrameLayout) findViewById(R.id.video_layout);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mp = new MediaPlayer();
        mRadioGroup.setOnCheckedChangeListener(this);
        mRadioGroup.setOnClickListener(this);

        mButtonStart.setOnClickListener(this);
        mButtonCamera.setOnClickListener(this);
        lightoff.setOnClickListener(this);
        mButtonVideo.setOnClickListener(this);

        userVo = Util.getUserVo(getApplicationContext());
        nickname = userVo.getNickname();
        secPass = userVo.getSec_pass_word();

        Util.checkCameraPermission(this);
        mPrefs = getApplicationContext().getSharedPreferences("stream", 0);
        editor = mPrefs.edit();
        editor.putString("stream", "false");
        editor.apply();
        mPrefs.registerOnSharedPreferenceChangeListener(listener);
        // Configures the SessionBuilder
        mSession = SessionBuilder.getInstance()
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_AAC)
                .setAudioQuality(new AudioQuality(8000, 16000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setSurfaceView(mSurfaceView)
                .setPreviewOrientation(0)
                .setCallback(this)
                .build();

        //mSession.setVideoQuality(new VideoQuality(640, 480, 15, 700 * 1000));
        // Configures the RTSP client
        mClient = new RtspClient();
        mClient.setSession(mSession);
        mClient.setCallback(this);

        // Use this to force streaming with the MediaRecorder API
        //mSession.getVideoTrack().setStreamingMethod(MediaStream.MODE_MEDIARECORDER_API);

        // Use this to stream over TCP, EXPERIMENTAL!
        //mClient.setTransportMode(RtspClient.TRANSPORT_TCP);

        // Use this if you want the aspect ratio of the surface view to
        // respect the aspect ratio of the camera preview
        //mSurfaceView.setAspectRatioMode(SurfaceView.ASPECT_RATIO_PREVIEW);

        mSurfaceView.getHolder().addCallback(this);

        Intent intent = getIntent();
        String state = intent.getStringExtra("state");
        if ("start".equals(state)) {
            toggleStream();
        }
    }

    public static void switchCam() {
        mSession.switchCamera();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mLayoutVideoSettings.setVisibility(View.GONE);
        selectQuality();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                mProgressBar.setVisibility(View.VISIBLE);
                toggleStream();
                break;
            case R.id.camera:
                switchCam();
                break;
            case R.id.videosettings:
                mRadioGroup.clearCheck();
                mLayoutVideoSettings.setVisibility(View.VISIBLE);
                break;
            case R.id.lightoff:
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                params.screenBrightness = 0;
                getWindow().setAttributes(params);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        editor.putString("stream", "false");
        editor.apply();
        mp.release();
        mClient.release();
        mSession.release();
        mSurfaceView.getHolder().removeCallback(this);

    }

    private void selectQuality() {
        int id = mRadioGroup.getCheckedRadioButtonId();

        RadioButton button = (RadioButton) findViewById(id);
        if (button == null)
            return;

        String text = button.getText().toString();
        Pattern pattern = Pattern.compile("(\\d+)x(\\d+)\\D+(\\d+)\\D+(\\d+)");
        Matcher matcher = pattern.matcher(text);

        matcher.find();
        int width = Integer.parseInt(matcher.group(1));
        int height = Integer.parseInt(matcher.group(2));
        int framerate = Integer.parseInt(matcher.group(3));
        int bitrate = Integer.parseInt(matcher.group(4)) * 1000;

        mSession.setVideoQuality(new VideoQuality(width, height, framerate, bitrate));
        Toast.makeText(this, width + " " + height + " " + framerate + " " + bitrate, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, ((RadioButton) findViewById(id)).getText(), Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Selected resolution: " + width + "x" + height);
    }


    private void enableUI() {
        mButtonStart.setEnabled(true);
        mButtonCamera.setEnabled(true);
    }

    // Connects/disconnects to the RTSP server and starts/stops the stream
    public void toggleStream() {

        if (!mClient.isStreaming()) {
            String ip, port, path;
            // We parse the URI written in the Editext
            ip = "150.95.141.66";
            port = "1935";
            path = "live/" + nickname + "/stream";
            mClient.setCredentials(nickname, secPass + "");
            mClient.setServerAddress(ip, Integer.parseInt(port));
            mClient.setStreamPath("/" + path);
            mClient.startStream();
            editor.putString("stream", "true");
            editor.apply();
        } else {
            // Stops the stream and disconnects from the RTSP server
            mClient.stopStream();
            editor.putString("stream", "false");
            editor.apply();
        }
    }

    public void mp3Player(String fileName) throws IOException {
        try {
            mp.reset();
            mp.setDataSource("http://150.95.141.66/hotdog/hotdog/image/user/" + fileName);
            mp.prepare();
            mp.setLooping(false);
            MediaPlayer.OnCompletionListener listener2 = new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    System.out.println("complection");
                    mp.stop();
                    mp.reset();
                }
            };
            mp.setOnCompletionListener(listener2);
            mp.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

        } catch (IllegalStateException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void logError(final String msg) {
        final String error = (msg == null) ? "Error unknown" : msg;
        // Displays a popup to report the eror to the user
        AlertDialog.Builder builder = new AlertDialog.Builder(StreamingActivity.this);
        builder.setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBitrateUpdate(long bitrate) {
        mTextBitrate.setText("" + bitrate / 1000 + " kbps");
    }

    @Override
    public void onPreviewStarted() {
        if (mSession.getCamera() == CameraInfo.CAMERA_FACING_FRONT) {
        }
    }

    @Override
    public void onSessionConfigured() {

    }

    @Override
    public void onSessionStarted() {
        enableUI();
        mButtonStart.setImageResource(R.drawable.ic_switch_video_active);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSessionStopped() {
        enableUI();
        mButtonStart.setImageResource(R.drawable.ic_switch_video);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {
        mProgressBar.setVisibility(View.GONE);
        switch (reason) {
            case Session.ERROR_CAMERA_ALREADY_IN_USE:
                break;
            case Session.ERROR_INVALID_SURFACE:
                break;
            case Session.ERROR_STORAGE_NOT_READY:
                break;
            case Session.ERROR_CONFIGURATION_NOT_SUPPORTED:
                VideoQuality quality = mSession.getVideoTrack().getVideoQuality();
                logError("The following settings are not supported on this phone: " +
                        quality.toString() + " " +
                        "(" + e.getMessage() + ")");
                e.printStackTrace();
                return;
            case Session.ERROR_OTHER:
                break;
        }

        if (e != null) {
            logError(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onRtspUpdate(int message, Exception e) {
        switch (message) {
            case RtspClient.ERROR_CONNECTION_FAILED:
            case RtspClient.ERROR_WRONG_CREDENTIALS:
                mProgressBar.setVisibility(View.GONE);
                enableUI();
                logError(e.getMessage());
                e.printStackTrace();
                break;
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSession.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mClient.stopStream();
        editor.putString("stream", "false");
        editor.apply();
    }
}