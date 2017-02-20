package com.hotdog.hotapp.fragment.home;

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
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.StreamingService;
import com.hotdog.hotapp.vo.PiVo;
import com.hotdog.hotapp.vo.UserVo;

import java.io.File;
import java.io.IOException;

public class StreamVideo2Fragment extends Fragment {
    private VideoView videoView;
    private StreamingService streamingService;
    private String VideoURL;
    private ProgressBar mProgressBar;
    private MediaRecorder recorder;
    private MediaController mediacontroller;
    private Uri video;
    private UserVo userVo;
    private PiVo piVo;
    private ImageButton start2, camera2, videosettings2, toggleVoice, toggleRec;
    private Boolean isChecked, isChecked1, isChecked2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stream_video2, container, false);
        Util.checkStoragePermission(getActivity());
        videoView = (VideoView) rootView.findViewById(R.id.videoView3);
        toggleVoice = (ImageButton) rootView.findViewById(R.id.toggleVoice2);
        start2 = (ImageButton) rootView.findViewById(R.id.start2);
        camera2 = (ImageButton) rootView.findViewById(R.id.camera2);
        videosettings2 = (ImageButton) rootView.findViewById(R.id.videosettings2);
        toggleRec = (ImageButton) rootView.findViewById(R.id.toggleRec2);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar2);
        mProgressBar.setVisibility(View.VISIBLE);
        isChecked = false;
        isChecked1 = false;
        isChecked2 = false;


        streamingService = new StreamingService();
        userVo = Util.getUserVo("userData", getActivity());
        piVo = Util.getPiVo("piData", getActivity());

        VideoURL = "rtsp://150.95.141.66:1935/live/" + userVo.getNickname() + "/stream";

        toggleVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked) {
                    isChecked = false;
                    toggleAudio(isChecked);
                    toggleVoice.setImageResource(R.drawable.microphoneblack);
                } else {
                    isChecked = true;
                    toggleAudio(isChecked);
                    toggleVoice.setImageResource(R.drawable.microphonered);
                }
            }
        });
        toggleRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked1) {
                    isChecked1 = false;
                    toggleRec(isChecked1);
                    toggleRec.setImageResource(R.drawable.recordblack);

                } else {
                    isChecked1 = true;
                    toggleRec(isChecked1);
                    toggleRec.setImageResource(R.drawable.recordred);

                }
            }
        });

        camera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        videosettings2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        start2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked1) {
                    isChecked1 = false;
                    toggleStream(isChecked1);
                } else {
                    isChecked1 = true;
                    toggleStream(isChecked1);
                }

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

    public void toggleStream(boolean isChecked) {
        if (isChecked) {
            new StartAsyncTask().execute();
        } else {
            new StopAsyncTask().execute();
        }
    }

    public void toggleRec(boolean isChecked) {
        if (isChecked) {
            new RecAsyncTask().execute();
        } else {
            new RecStopAsyncTask().execute();
        }
    }


    public void toggleAudio(boolean isChecked) {
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

        }

    }

    //스트리밍 시작
    private class StartAsyncTask extends SafeAsyncTask<Integer> {

        @Override
        public Integer call() throws Exception {
            return streamingService.moBileController("start", "cSOSTNzupFU:APA91bGrCDI02lAbO2WVfveVw2-sDIwfoKekL41e-hLT1BUDHohiOLBG7mQEGyNJ80h-WyqGsH9SHOMtOMrBise3Zdd0bS-E4LlCfHUsxPhUlJiYNeGMFKDTl9PpVLbOQB_F5LdrkctQ");
        }

        @Override
        protected void onSuccess(Integer integer) throws Exception {

        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }
    }

    //스트리밍 종료
    private class StopAsyncTask extends SafeAsyncTask<Integer> {

        @Override
        public Integer call() throws Exception {
            return streamingService.moBileController("stop", "cSOSTNzupFU:APA91bGrCDI02lAbO2WVfveVw2-sDIwfoKekL41e-hLT1BUDHohiOLBG7mQEGyNJ80h-WyqGsH9SHOMtOMrBise3Zdd0bS-E4LlCfHUsxPhUlJiYNeGMFKDTl9PpVLbOQB_F5LdrkctQ");
        }

        @Override
        protected void onSuccess(Integer integer) throws Exception {
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }
    }

    //카메라 변경
    private class CameraAsyncTask extends SafeAsyncTask<Integer> {

        @Override
        public Integer call() throws Exception {
            return streamingService.moBileController("camera", "cSOSTNzupFU:APA91bGrCDI02lAbO2WVfveVw2-sDIwfoKekL41e-hLT1BUDHohiOLBG7mQEGyNJ80h-WyqGsH9SHOMtOMrBise3Zdd0bS-E4LlCfHUsxPhUlJiYNeGMFKDTl9PpVLbOQB_F5LdrkctQ");
        }

        @Override
        protected void onSuccess(Integer integer) throws Exception {
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }
    }

    //화질 변경
    private class QualityAsyncTask extends SafeAsyncTask<Integer> {

        @Override
        public Integer call() throws Exception {
            return null;
        }

        @Override
        protected void onSuccess(Integer integer) throws Exception {
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }
    }

    //녹화 시작
    private class RecAsyncTask extends SafeAsyncTask<Integer> {

        @Override
        public Integer call() throws Exception {
            return streamingService.recStart(userVo.getNickname(), userVo.getUsers_no());
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(Integer flag) throws Exception {
            Toast.makeText(getActivity(), "녹화 시작", Toast.LENGTH_SHORT).show();
        }
    }

    //녹화 종료
    private class RecStopAsyncTask extends SafeAsyncTask<Integer> {

        @Override
        public Integer call() throws Exception {
            return streamingService.recStop(userVo.getNickname());
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(Integer flag) throws Exception {
            Toast.makeText(getActivity(), "녹화 종료", Toast.LENGTH_SHORT).show();
        }
    }
}