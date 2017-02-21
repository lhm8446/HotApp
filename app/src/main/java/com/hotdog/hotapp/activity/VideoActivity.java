/*
 *
 * Copyright (c) 2010-2014 EVE GROUP PTE. LTD.
 *
 */

package com.hotdog.hotapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.MediaRecorder;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.mediaplayer.test.SharedSettings;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.StreamingService;
import com.hotdog.hotapp.vo.PiVo;
import com.hotdog.hotapp.vo.UserVo;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import cn.refactor.lib.colordialog.PromptDialog;
import veg.mediaplayer.sdk.MediaPlayer;
import veg.mediaplayer.sdk.MediaPlayer.PlayerNotifyCodes;
import veg.mediaplayer.sdk.MediaPlayer.PlayerProperties;
import veg.mediaplayer.sdk.MediaPlayer.VideoShot;
import veg.mediaplayer.sdk.MediaPlayerConfig;


public class VideoActivity extends Activity implements OnClickListener, MediaPlayer.MediaPlayerCallback {
    private static final String TAG = "MediaPlayerTest";

    private ImageButton btnConnect;
    private ImageButton btnShot;

    private StatusProgressTask mProgressTask = null;
    private LinearLayout linearLayout1;

    private boolean playing = false;
    private MediaPlayer player = null;
    private VideoActivity mthis = null;

    private RelativeLayout playerStatus = null;
    private TextView playerStatusText = null;
    private TextView playerHwStatus = null;


    private MulticastLock multicastLock = null;

    private StreamingService streamingService;
    private String VideoURL;
    private UserVo userVo;
    private PiVo piVo;
    private ImageButton stop2, camera2, videosettings2, toggleVoice, toggleRec;
    private Boolean isChecked, isChecked1, isChecked2;
    private SharedPreferences wifiChk;
    private MediaRecorder recorder;

    private enum PlayerStates {
        Busy,
        ReadyForUse
    }

    private enum PlayerConnectType {
        Normal,
        Reconnecting
    }

    private PlayerStates player_state = PlayerStates.ReadyForUse;
    private PlayerConnectType reconnect_type = PlayerConnectType.Normal;
    private int mOldMsg = 0;
    private Toast toastShot = null;

    // Event handler
    private Handler handler = new Handler() {
        String strText = "Connecting";

        String sText;
        String sCode;

        @Override
        public void handleMessage(Message msg) {
            PlayerNotifyCodes status = (PlayerNotifyCodes) msg.obj;
            switch (status) {
                case CP_CONNECT_STARTING:
                    if (reconnect_type == PlayerConnectType.Reconnecting)
                        strText = "Reconnecting";
                    else
                        strText = "Connecting";

                    startProgressTask(strText);
                    player_state = PlayerStates.Busy;
                    showStatusView();
                    reconnect_type = PlayerConnectType.Normal;
                    setHideControls();
                    break;

                case PLP_BUILD_SUCCESSFUL:
                    sText = player.getPropString(PlayerProperties.PP_PROPERTY_PLP_RESPONSE_TEXT);
                    sCode = player.getPropString(PlayerProperties.PP_PROPERTY_PLP_RESPONSE_CODE);
                    Log.i(TAG, "=Status PLP_BUILD_SUCCESSFUL: Response sText=" + sText + " sCode=" + sCode);
                    break;

                case VRP_NEED_SURFACE:
                    player_state = PlayerStates.Busy;
                    showVideoView();
                    break;

                case PLP_PLAY_SUCCESSFUL:
                    player_state = PlayerStates.ReadyForUse;
                    stopProgressTask();
                    playerStatusText.setText("");
                    setTitle(R.string.app_name);
                    break;

                case PLP_CLOSE_STARTING:
                    player_state = PlayerStates.Busy;
                    stopProgressTask();
                    playerStatusText.setText("Disconnected");
                    showStatusView();
                    setUIDisconnected();

                    break;

                case PLP_CLOSE_SUCCESSFUL:
                    player_state = PlayerStates.ReadyForUse;
                    stopProgressTask();
                    playerStatusText.setText("Disconnected");
                    showStatusView();
                    System.gc();
                    setShowControls();
                    setUIDisconnected();
                    break;

                case PLP_CLOSE_FAILED:
                    player_state = PlayerStates.ReadyForUse;
                    stopProgressTask();
                    playerStatusText.setText("Disconnected");
                    showStatusView();
                    setShowControls();
                    setUIDisconnected();
                    break;

                case CP_CONNECT_FAILED:
                    player_state = PlayerStates.ReadyForUse;
                    stopProgressTask();
                    playerStatusText.setText("Disconnected");
                    showStatusView();
                    setShowControls();
                    setUIDisconnected();
                    break;

                case PLP_BUILD_FAILED:
                    sText = player.getPropString(PlayerProperties.PP_PROPERTY_PLP_RESPONSE_TEXT);
                    sCode = player.getPropString(PlayerProperties.PP_PROPERTY_PLP_RESPONSE_CODE);
                    Log.i(TAG, "=Status PLP_BUILD_FAILED: Response sText=" + sText + " sCode=" + sCode);

                    player_state = PlayerStates.ReadyForUse;
                    stopProgressTask();
                    playerStatusText.setText("Disconnected");
                    showStatusView();
                    setShowControls();
                    setUIDisconnected();
                    break;

                case PLP_PLAY_FAILED:
                    player_state = PlayerStates.ReadyForUse;
                    stopProgressTask();
                    playerStatusText.setText("Disconnected");
                    showStatusView();
                    setShowControls();
                    setUIDisconnected();
                    break;

                case PLP_ERROR:
                    player_state = PlayerStates.ReadyForUse;
                    stopProgressTask();
                    playerStatusText.setText("Disconnected");
                    showStatusView();
                    setShowControls();
                    setUIDisconnected();
                    break;

                case CP_INTERRUPTED:
                    player_state = PlayerStates.ReadyForUse;
                    stopProgressTask();
                    playerStatusText.setText("Disconnected");
                    showStatusView();
                    setShowControls();
                    setUIDisconnected();
                    break;

                //case CONTENT_PROVIDER_ERROR_DISCONNECTED:
                case CP_STOPPED:
                case VDP_STOPPED:
                case VRP_STOPPED:
                case ADP_STOPPED:
                case ARP_STOPPED:
                    if (player_state != PlayerStates.Busy) {
                        stopProgressTask();
                        player_state = PlayerStates.Busy;
                        if (toastShot != null)
                            toastShot.cancel();
                        player.Close();
                        playerStatusText.setText("Disconnected");
                        showStatusView();
                        player_state = PlayerStates.ReadyForUse;
                        setShowControls();
                        setUIDisconnected();
                    }
                    break;

                case CP_ERROR_DISCONNECTED:
                    if (player_state != PlayerStates.Busy) {
                        player_state = PlayerStates.Busy;
                        if (toastShot != null)
                            toastShot.cancel();
                        player.Close();

                        playerStatusText.setText("Disconnected");
                        showStatusView();
                        player_state = PlayerStates.ReadyForUse;
                        setUIDisconnected();


                    }
                    break;
                default:
                    player_state = PlayerStates.Busy;
            }
        }
    };

    // callback from Native Player
    @Override
    public int OnReceiveData(ByteBuffer buffer, int size, long pts) {
        Log.e(TAG, "Form Native Player OnReceiveData: size: " + size + ", pts: " + pts);
        return 0;
    }


    // All event are sent to event handlers
    @Override
    public int Status(int arg) {
        PlayerNotifyCodes status = PlayerNotifyCodes.forValue(arg);
        if (handler == null || status == null)
            return 0;

        Log.e(TAG, "Form Native Player status: " + arg);
        switch (PlayerNotifyCodes.forValue(arg)) {
            default:
                Message msg = new Message();
                msg.obj = status;
                handler.removeMessages(mOldMsg);
                mOldMsg = msg.what;
                handler.sendMessage(msg);
        }
        return 0;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        multicastLock = wifi.createMulticastLock("multicastLock");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();

        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

        setContentView(R.layout.activity_video);
        mthis = this;

        SharedSettings.getInstance(this).loadPrefSettings();
        SharedSettings.getInstance().savePrefSettings();
        linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        playerStatus = (RelativeLayout) findViewById(R.id.playerStatus);
        playerStatusText = (TextView) findViewById(R.id.playerStatusText);
        playerHwStatus = (TextView) findViewById(R.id.playerHwStatus);
        btnShot = (ImageButton) findViewById(R.id.button_shot);
        player = (MediaPlayer) findViewById(R.id.playerView);
        btnConnect = (ImageButton) findViewById(R.id.button_connect);
        toggleVoice = (ImageButton) findViewById(R.id.toggleVoice2);
        stop2 = (ImageButton) findViewById(R.id.stop2);
        camera2 = (ImageButton) findViewById(R.id.camera2);
        videosettings2 = (ImageButton) findViewById(R.id.videosettings2);
        toggleRec = (ImageButton) findViewById(R.id.toggleRec2);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_view);

        wifiChk = getSharedPreferences("wifiChk", 0);
        isChecked = false;
        isChecked1 = false;
        isChecked2 = false;

        streamingService = new StreamingService();
        userVo = Util.getUserVo(this);
        piVo = Util.getPiVo(this);

        VideoURL = "rtsp://150.95.141.66:1935/live/" + userVo.getNickname() + "/stream";

        buttonInit();

        player.getSurfaceView().setZOrderOnTop(true);    // necessary
        SurfaceHolder sfhTrackHolder = player.getSurfaceView().getHolder();
        sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Array of choices
        btnShot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (player != null) {
                    Log.e("SDL", "getVideoShot()");

                    //VideoShot frame = player.getVideoShot(200, 200);
                    VideoShot frame = player.getVideoShot(-1, -1);
                    if (frame == null)
                        return;
                    // get your custom_toast.xml ayout
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.videoshot_view,
                            (ViewGroup) findViewById(R.id.videoshot_toast_layout_id));

                    ImageView image = (ImageView) layout.findViewById(R.id.videoshot_image);
                    image.setImageBitmap(getFrameAsBitmap(frame.getData(), frame.getWidth(), frame.getHeight()));

                    // Toast...
                    if (toastShot != null)
                        toastShot.cancel();

                    toastShot = new Toast(getApplicationContext());
                    toastShot.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toastShot.setDuration(Toast.LENGTH_SHORT);
                    toastShot.setView(layout);
                    toastShot.show();

                }
            }
        });

        btnConnect.setOnClickListener(this);
        setShowControls();
    }

    public void buttonInit() {
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
        stop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StopAsyncTask().execute();
                isChecked2 = false;
                onBackPressed();
            }
        });
        camera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CameraAsyncTask().execute();
            }
        });
        videosettings2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

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

    public Bitmap getFrameAsBitmap(ByteBuffer frame, int width, int height) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(frame);
        return bmp;
    }

    public void onClick(View v) {
        int wifi1 = Util.getConnectivityStatus(this);
        if (wifi1 != 1 && wifiChk.getBoolean("chk", false)) {
            new PromptDialog(this)
                    .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                    .setAnimationEnable(true)
                    .setTitleText("info")
                    .setContentText("wifi 상태가 아닙니다. \n  데이터를 사용하실려면 Settings에서 변경하세요.")
                    .setPositiveListener("확인", new PromptDialog.OnPositiveListener() {
                        @Override
                        public void onClick(PromptDialog dialog) {
                            dialog.dismiss();
                        }
                    }).show();
        } else {
            if ("Disconnected".equals(playerStatusText.getText().toString())) {
                if (!isChecked2) {
                    new StartAsyncTask().execute();
                    isChecked2 = true;
                }
            }

            SharedSettings.getInstance().loadPrefSettings();
            if (player != null) {
                player.getConfig().setConnectionUrl(VideoURL);
                if (player.getConfig().getConnectionUrl().isEmpty()) {
                    new StartAsyncTask().execute();
                    return;
                }
                if (toastShot != null)
                    toastShot.cancel();

                player.Close();
                if (playing) {
                    setUIDisconnected();
                } else {
                    SharedSettings sett = SharedSettings.getInstance();
                    boolean bPort = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
                    int aspect = bPort ? 1 : 0;

                    MediaPlayerConfig conf = new MediaPlayerConfig();

                    player.setVisibility(View.INVISIBLE);
                    conf.setConnectionUrl(player.getConfig().getConnectionUrl());
                    conf.setConnectionNetworkProtocol(sett.connectionProtocol);
                    conf.setConnectionDetectionTime(sett.connectionDetectionTime);
                    conf.setConnectionBufferingTime(sett.connectionBufferingTime);
                    conf.setDecodingType(sett.decoderType);
                    conf.setRendererType(sett.rendererType);
                    conf.setSynchroEnable(sett.synchroEnable);
                    conf.setSynchroNeedDropVideoFrames(sett.synchroNeedDropVideoFrames);
                    conf.setEnableColorVideo(sett.rendererEnableColorVideo);
                    conf.setEnableAspectRatio(aspect);
                    conf.setDataReceiveTimeout(30000);
                    conf.setNumberOfCPUCores(0);

                    // Open Player
                    player.Open(conf, mthis);
                    playing = true;
                }
            }
        }
    }

    protected void onPause() {
        Log.e("SDL", "onPause()");
        super.onPause();

        if (player != null)
            player.onPause();
    }

    @Override
    protected void onResume() {
        Log.e("SDL", "onResume()");
        super.onResume();
        if (player != null)
            player.onResume();
    }

    @Override
    protected void onStart() {
        Log.e("SDL", "onStart()");
        super.onStart();
        if (player != null)
            player.onStart();
    }

    @Override
    protected void onStop() {
        Log.e("SDL", "onStop()");
        super.onStop();
        if (player != null)
            player.onStop();

        if (toastShot != null)
            toastShot.cancel();

    }

    @Override
    public void onBackPressed() {
        if (toastShot != null)
            toastShot.cancel();

        player.Close();
        if (!playing) {
            super.onBackPressed();
            return;
        }

        setUIDisconnected();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.e("SDL", "onWindowFocusChanged(): " + hasFocus);
        super.onWindowFocusChanged(hasFocus);
        if (player != null)
            player.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onLowMemory() {
        Log.e("SDL", "onLowMemory()");
        super.onLowMemory();
        if (player != null)
            player.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        Log.e("SDL", "onDestroy()");
        if (toastShot != null)
            toastShot.cancel();

        if (player != null)
            player.onDestroy();

        stopProgressTask();
        System.gc();

        if (multicastLock != null) {
            multicastLock.release();
            multicastLock = null;
        }
        super.onDestroy();
    }


    protected void setUIDisconnected() {
        playing = false;
    }

    protected void setHideControls() {
        linearLayout1.setVisibility(View.VISIBLE);
        btnConnect.setVisibility(View.GONE);
    }

    protected void setShowControls() {
        setTitle(R.string.app_name);
        linearLayout1.setVisibility(View.GONE);
        btnConnect.setVisibility(View.VISIBLE);
    }

    private void showStatusView() {
        player.setVisibility(View.INVISIBLE);
        playerHwStatus.setVisibility(View.INVISIBLE);
        //player.setAlpha(0.0f);
        playerStatus.setVisibility(View.VISIBLE);

    }

    private void showVideoView() {
        playerStatus.setVisibility(View.INVISIBLE);
        player.setVisibility(View.VISIBLE);
        playerHwStatus.setVisibility(View.VISIBLE);

        SurfaceHolder sfhTrackHolder = player.getSurfaceView().getHolder();
        sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);

        setTitle("");
    }

    private void startProgressTask(String text) {
        stopProgressTask();

        mProgressTask = new StatusProgressTask(text);
        //mProgressTask.execute(text);
        executeAsyncTask(mProgressTask, text);
    }

    private void stopProgressTask() {
        playerStatusText.setText("");
        setTitle(R.string.app_name);

        if (mProgressTask != null) {
            mProgressTask.stopTask();
            mProgressTask.cancel(true);
        }
    }

    private class StatusProgressTask extends AsyncTask<String, Void, Boolean> {
        String strProgressTextSrc;
        String strProgressText;
        Rect bounds = new Rect();
        boolean stop = false;

        public StatusProgressTask(String text) {
            stop = false;
            strProgressTextSrc = text;
        }

        public void stopTask() {
            stop = true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                if (stop) return true;

                String maxText = "Disconnected.....";//strProgressTextSrc + "....";
                int len = maxText.length();
                playerStatusText.getPaint().getTextBounds(maxText, 0, len, bounds);

                strProgressText = strProgressTextSrc + "...";

                Runnable uiRunnable = null;
                uiRunnable = new Runnable() {
                    public void run() {
                        if (stop) return;

                        playerStatusText.setText(strProgressText);

                        RelativeLayout.LayoutParams layoutParams =
                                (RelativeLayout.LayoutParams) playerStatusText.getLayoutParams();

                        layoutParams.width = bounds.width();
                        playerStatusText.setLayoutParams(layoutParams);
                        playerStatusText.setGravity(Gravity.NO_GRAVITY);

                        synchronized (this) {
                            this.notify();
                        }
                    }
                };

                int nCount = 4;
                do {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        stop = true;
                    }

                    if (stop) break;

                    if (nCount <= 3) {
                        strProgressText = strProgressTextSrc;
                        for (int i = 0; i < nCount; i++)
                            strProgressText = strProgressText + ".";
                    }

                    synchronized (uiRunnable) {
                        runOnUiThread(uiRunnable);
                        try {
                            uiRunnable.wait();
                        } catch (InterruptedException e) {
                            stop = true;
                        }
                    }

                    if (stop) break;

                    nCount++;
                    if (nCount > 3) {
                        nCount = 1;
                        strProgressText = strProgressTextSrc;
                    }
                }

                while (!isCancelled());
            } catch (Exception e) {
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mProgressTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    static public <T> void executeAsyncTask(AsyncTask<T, ?, ?> task, T... params) {
        {
            task.execute(params);
        }
    }


    //스트리밍 시작
    private class StartAsyncTask extends SafeAsyncTask<Integer> {

        @Override
        public Integer call() throws Exception {
            return streamingService.mobileController("start", piVo.getSec_token());
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
            return streamingService.mobileController("stop", piVo.getSec_token());
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
            return streamingService.mobileController("camera", piVo.getSec_token());
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
            Toast.makeText(getApplicationContext(), "녹화 시작", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "녹화 종료", Toast.LENGTH_SHORT).show();
        }
    }
}
