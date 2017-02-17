package com.hotdog.hotapp.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.fragment.vod.ListViewSmallScreenFragment;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.listvideoplay.ListDataGenerater;
import com.hotdog.hotapp.other.listvideoplay.VideoModel;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.VodService;
import com.hotdog.hotapp.vo.UserVo;
import com.hotdog.hotapp.vo.VideoVo;

import java.util.ArrayList;
import java.util.HashMap;

public class VodActivity extends AppCompatActivity {
    private VodService vodService;
    private UserVo userVo;
    private final String URL = "http://150.95.141.66:80/hotdog/hotdog/image/user/";
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar1);
        mProgressBar.setVisibility(View.VISIBLE);
        userVo = Util.getUserVo("userData", getApplicationContext());
        new getVodUrl().execute();
    }

    @Override
    public void onBackPressed() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            super.onBackPressed();
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("userNo", Util.getUserVo("userData", getApplicationContext()).getUsers_no());
            startActivity(intent);
            finish();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    public String getSize(String url) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(url, new HashMap<String, String>());
        String duration =
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long dur = Long.parseLong(duration);
        String secondss = String.valueOf((dur % 60000) / 1000);
        int total = Integer.parseInt(secondss);
        int hour = total / 3600;
        total = total % 3600;
        int min = total / 60;
        total = total % 60;
        int sec = total;
        if (hour == 0) {
            if (sec < 10) {
                return min + ":0" + sec;
            }
            return min + ":" + sec;
        } else {
            return hour + ":" + min + ":" + sec;
        }

    }

    private class getVodUrl extends SafeAsyncTask<ArrayList<VideoVo>> {

        @Override
        public ArrayList<VideoVo> call() throws Exception {
            vodService = new VodService();
            return vodService.fetchVodUrl(userVo);
        }

        @Override
        protected void onSuccess(ArrayList<VideoVo> urlList) throws Exception {
            ListDataGenerater.clearDatas();
            for (VideoVo videoVo : urlList) {
                VideoModel videoModel = new VideoModel();
                videoModel.videoUrl = URL + "5/" + videoVo.getSave_name();
                videoModel.title = videoVo.getRegdate() + " " + videoVo.getRegtime();
                //videoModel.size = getSize(videoModel.videoUrl);
                ListDataGenerater.addDatas(videoModel);
            }
            mProgressBar.setVisibility(View.GONE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_container, new ListViewSmallScreenFragment(), "ListViewSmallScreenFragment")
                    .addToBackStack(null)
                    .commit();
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }
    }
}
