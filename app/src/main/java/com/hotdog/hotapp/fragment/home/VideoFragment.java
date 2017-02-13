package com.hotdog.hotapp.fragment.home;

import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.network.SafeAsyncTask;
import com.hotdog.hotapp.service.StreamingService;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.rtsp.RtspClient;

import java.io.File;
import java.io.IOException;

public class VideoFragment extends Fragment implements RtspClient.Callback,
        Session.Callback {
    private Session mSession;
    private RtspClient mClient;
    ToggleButton toggleVoice;
    TextView textBitrate;
    VideoView videoView;
    StreamingService streamingService;
    SharedPreferences baseSetting;
    String nickname, ipNumber;
    int secPasss;
    String VideoURL;
    ProgressBar mProgressBar;
    MediaRecorder recorder;
    MediaController mediacontroller;
    Uri video;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_video, container, false);
        videoView = (VideoView) rootView.findViewById(R.id.videoView2);
        toggleVoice = (ToggleButton) rootView.findViewById(R.id.toggleVoice);
        Button buttonRight = (Button) rootView.findViewById(R.id.buttonRight);
        Button buttonLeft = (Button) rootView.findViewById(R.id.buttonLeft);
        Button buttonCenter = (Button) rootView.findViewById(R.id.buttonCenter);
        ToggleButton toggleRec = (ToggleButton) rootView.findViewById(R.id.toggleRec);
        textBitrate = (TextView) rootView.findViewById(R.id.bitrate);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        toggleVoice.setText("");
        toggleVoice.setTextOff("");
        toggleRec.setText("");
        toggleRec.setTextOn("rec");

        streamingService = new StreamingService();
        //audioInit();


        baseSetting = this.getActivity().getSharedPreferences("setting", 0);
        nickname = baseSetting.getString("nickname", "none");
        ipNumber = baseSetting.getString("ipnumber", "");
        secPasss = baseSetting.getInt("secpass", 0);

        VideoURL = "rtsp://150.95.141.66:1935/live/" + nickname + "/stream";


        toggleVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toggleRecord(isChecked);
                } else {
                    toggleRecord(isChecked);
                }
            }
        });

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PiControllAsyncTask("left", ipNumber).execute();
            }
        });
        buttonCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PiControllAsyncTask("center", ipNumber).execute();
            }
        });
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PiControllAsyncTask("right", ipNumber).execute();
            }
        });

        // Start the MediaController
        mediacontroller = new MediaController(getActivity());
        mediacontroller.setAnchorView(videoView);
        // Get the URL from String VideoURL
        video = Uri.parse(VideoURL);
        videoView.setMediaController(mediacontroller);
        videoView.setVideoURI(video);

        videoView.requestFocus();
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                new PiControllAsyncTask("streamstop", ipNumber).execute();
                new PiControllAsyncTask("stream", ipNumber).execute();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        new PiControllAsyncTask(nickname + "," + secPasss, ipNumber).execute();
                    }
                }).start();
                return false;
            }

        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                mProgressBar.setVisibility(View.GONE);
                videoView.start();
            }
        });

        return rootView;
    }

    public void audioInit() {
        mSession = SessionBuilder.getInstance()
                .setContext(getActivity())
                .setAudioEncoder(SessionBuilder.AUDIO_AAC)
                .setAudioQuality(new AudioQuality(8000, 16000))
                .setVideoEncoder(SessionBuilder.VIDEO_NONE)
                .setPreviewOrientation(0)
                .setCallback(this)
                .build();

        mClient = new RtspClient();
        mClient.setSession(mSession);
        mClient.setCallback(this);

    }

    public void toggleStream() {
        if (!mClient.isStreaming()) {
            String ip, port, path;

            ip = "150.95.141.66";
            port = "1935";
            path = "live/" + nickname + "/audio";

            mClient.setCredentials(nickname, secPasss + "");
            mClient.setServerAddress(ip, Integer.parseInt(port));
            mClient.setStreamPath("/" + path);
            mClient.startStream();

        } else {
            // Stops the stream and disconnects from the RTSP server
            mClient.stopStream();
        }
    }

    public void toggleRecord(boolean isChecked) {
        if (isChecked) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setOutputFile(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/myrecord.mp3");
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            try {
                recorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            recorder.start();
        } else {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;

            File saveFile = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/myrecord.mp3");

            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/myrecord.mp3");

            String duration =
                    metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Log.v("time", duration);
            long dur = Long.parseLong(duration);
            String secondss = String.valueOf((dur % 60000) / 1000);
            int seconds = Integer.parseInt(secondss);

            new AudioUploadAsyncTask(saveFile, seconds).execute();
        }

    }

    @Override
    public void onBitrateUpdate(long bitrate) {
        textBitrate.setText("" + bitrate / 1000 + " kbps");
    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {

    }

    @Override
    public void onPreviewStarted() {

    }

    @Override
    public void onSessionConfigured() {

    }

    @Override
    public void onSessionStarted() {
        toggleVoice.setChecked(true);
    }

    @Override
    public void onSessionStopped() {
        toggleVoice.setChecked(false);
    }

    @Override
    public void onRtspUpdate(int message, Exception exception) {

    }

    private class PiControllAsyncTask extends SafeAsyncTask<String> {
        String msg;
        String ip;

        PiControllAsyncTask(String msg, String ip) {
            this.msg = msg;
            this.ip = ip;
        }

        @Override
        public String call() throws Exception {
            return streamingService.piController(msg, ip);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(String flag) throws Exception {

        }
    }

    private class AudioUploadAsyncTask extends SafeAsyncTask<String> {
        File file;
        int seconds;

        AudioUploadAsyncTask(File file, int seconds) {
            this.file = file;
            this.seconds = seconds;
        }

        @Override
        public String call() throws Exception {
            return streamingService.audioUpload(file);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(final String filename) throws Exception {
            Toast.makeText(getActivity(), filename, Toast.LENGTH_SHORT).show();
            new PiControllAsyncTask("audio", ipNumber).execute();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new PiControllAsyncTask(filename, ipNumber).execute();
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        Thread.sleep(seconds * 2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new PiControllAsyncTask("audiostop", ipNumber).execute();
                }
            }).start();

        }
    }
}