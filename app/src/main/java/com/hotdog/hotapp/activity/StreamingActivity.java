package com.hotdog.hotapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;
import net.majorkernelpanic.streaming.video.VideoQuality;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamingActivity extends Activity implements
        OnClickListener,
        RtspClient.Callback,
        Session.Callback,
        SurfaceHolder.Callback,
        OnCheckedChangeListener {

    public final static String TAG = "StreamingActivity";

    private ImageButton mButtonVideo;
    private ImageButton mButtonStart;
    private ImageButton mButtonFlash;
    private ImageButton mButtonCamera;
    private RadioGroup mRadioGroup;
    private FrameLayout mLayoutVideoSettings;
    private SurfaceView mSurfaceView;
    private TextView mTextBitrate;
    private ProgressBar mProgressBar;
    private Session mSession;
    private RtspClient mClient;
    private SharedPreferences baseSetting;
    private String nickname, ipNumber;
    private int secPasss;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 32;
    private static final int MY_PERMISSIONS_RECORD_AUDIO = 33;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_streaming);
        mButtonVideo = (ImageButton) findViewById(R.id.videosettings);
        mButtonStart = (ImageButton) findViewById(R.id.start);
        mButtonFlash = (ImageButton) findViewById(R.id.flash);
        mButtonCamera = (ImageButton) findViewById(R.id.camera);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mTextBitrate = (TextView) findViewById(R.id.bitrate);
        mLayoutVideoSettings = (FrameLayout) findViewById(R.id.video_layout);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mRadioGroup.setOnCheckedChangeListener(this);
        mRadioGroup.setOnClickListener(this);

        mButtonStart.setOnClickListener(this);
        mButtonFlash.setOnClickListener(this);
        mButtonCamera.setOnClickListener(this);
        mButtonVideo.setOnClickListener(this);
        mButtonFlash.setTag("off");

        checkPermission(this);

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(StreamingActivity.this);

        baseSetting = this.getSharedPreferences("setting", 0);
        nickname = baseSetting.getString("nickname", "none");
        ipNumber = baseSetting.getString("ipnumber", "");
        secPasss = baseSetting.getInt("secpass", 0);

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
        selectQuality();
        // mSession.setVideoQuality(new VideoQuality(352, 288, 30, 300000));
        mSession.setPreviewOrientation(90);
        mSession.configure();

        Intent intent = getIntent();
        String state = intent.getStringExtra("state");
        if ("start".equals(state)) {
            toggleStream();
            mSession.switchCamera();
        }


    }

    //    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            //READ_EXTERNAL_STORAGE( 사용권한이 없을 경우  -1)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                // 최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.RECORD_AUDIO)) {
                    // 사용자가 임의로 권한을 취소 시킨경우, 권한 재요청
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, StreamingActivity.MY_PERMISSIONS_RECORD_AUDIO);
                } else {
                    //최초로 권한을 요청하는경우 (첫실행)
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, StreamingActivity.MY_PERMISSIONS_RECORD_AUDIO);
                    return false;
                }
            }

            //CAMERA
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, StreamingActivity.MY_PERMISSIONS_REQUEST_CAMERA);

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, StreamingActivity.MY_PERMISSIONS_REQUEST_CAMERA);
                    return false;
                }
            }
            return true;
        } else {
            //사용 권한이 있음을 확인한 경우.
            return true;
        }
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
                toggleStream();
                break;
            case R.id.flash:
                if (mButtonFlash.getTag().equals("on")) {
                    mButtonFlash.setTag("off");
                    mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
                } else {
                    mButtonFlash.setImageResource(R.drawable.ic_flash_off_holo_light);
                    mButtonFlash.setTag("on");
                }
                mSession.toggleFlash();
                break;
            case R.id.camera:
                mSession.switchCamera();
                break;

            case R.id.videosettings:
                mRadioGroup.clearCheck();
                mLayoutVideoSettings.setVisibility(View.VISIBLE);
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        mProgressBar.setVisibility(View.VISIBLE);
        if (!mClient.isStreaming()) {
            String ip, port, path;

            // We save the content user inputs in Shared Preferences
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(StreamingActivity.this);
            Editor editor = mPrefs.edit();

            // We parse the URI written in the Editext
            Pattern uri = Pattern.compile("rtsp://(.+):(\\d*)/(.+)");
            ip = "150.95.141.66";
            port = "1935";
            path = "live/" + nickname + "/stream";

            mClient.setCredentials(nickname, secPasss + "");
            mClient.setServerAddress(ip, Integer.parseInt(port));
            mClient.setStreamPath("/" + path);
            mClient.startStream();

        } else {
            // Stops the stream and disconnects from the RTSP server
            mClient.stopStream();
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
            mButtonFlash.setEnabled(false);
            mButtonFlash.setTag("off");
            mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
        } else {
            mButtonFlash.setEnabled(true);
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
            case Session.ERROR_CAMERA_HAS_NO_FLASH:
                mButtonFlash.setImageResource(R.drawable.ic_flash_on_holo_light);
                mButtonFlash.setTag("off");
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
    }
}