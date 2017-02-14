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
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.StreamingService;
import com.hotdog.hotapp.vo.PiVo;
import com.hotdog.hotapp.vo.UserVo;

import java.io.File;
import java.io.IOException;

public class VideoFragment extends Fragment {
    private ToggleButton toggleVoice;
    private TextView textBitrate;
    private VideoView videoView;
    private StreamingService streamingService;
    private String VideoURL;
    private ProgressBar mProgressBar;
    private MediaRecorder recorder;
    private MediaController mediacontroller;
    private Uri video;
    private SharedPreferences wifiChk;
    private UserVo userVo;
    private PiVo piVo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_video, container, false);
        Util.checkStoragePermission(getActivity());
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

        wifiChk = getActivity().getSharedPreferences("wifiChk", 0);
        int wifi = Util.getConnectivityStatus(getActivity());

        streamingService = new StreamingService();

        userVo = Util.getUserVo("userData", getActivity());
        piVo = Util.getPiVo("piData", getActivity());


        VideoURL = "rtsp://150.95.141.66:1935/live/" + userVo.getNickname() + "/stream";


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
                new PiControllAsyncTask("left", piVo.getDevice_num()).execute();
            }
        });
        buttonCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PiControllAsyncTask("center", piVo.getDevice_num()).execute();
            }
        });
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PiControllAsyncTask("right", piVo.getDevice_num()).execute();
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

                new PiControllAsyncTask("streamstop", piVo.getDevice_num()).execute();
                new PiControllAsyncTask("stream", piVo.getDevice_num()).execute();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        new PiControllAsyncTask(userVo.getNickname() + "," + userVo.getSec_pass_word(), piVo.getDevice_num()).execute();
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
            long dur = Long.parseLong(duration);
            String secondss = String.valueOf((dur % 60000) / 1000);
            int seconds = Integer.parseInt(secondss);

            new AudioUploadAsyncTask(saveFile, seconds).execute();
        }

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
            new PiControllAsyncTask("audio", piVo.getDevice_num()).execute();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new PiControllAsyncTask(filename, piVo.getDevice_num()).execute();
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
                    new PiControllAsyncTask("audiostop", piVo.getDevice_num()).execute();
                }
            }).start();

        }
    }
}