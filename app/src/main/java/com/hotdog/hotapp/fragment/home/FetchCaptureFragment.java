package com.hotdog.hotapp.fragment.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.PicassoImageLoader;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.VodService;
import com.hotdog.hotapp.vo.CaptureVo;
import com.hotdog.hotapp.vo.UserVo;
import com.veinhorn.scrollgalleryview.MediaInfo;

import java.util.ArrayList;
import java.util.List;


public class FetchCaptureFragment extends Fragment {
    private ProgressBar mProgressBar;
    private static final String URL = "http://150.95.141.66/hotdog/hotdog/image/user/";
    private UserVo userVo;
    private VodService vodService;
    private List<MediaInfo> infos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_fetch_capture, container, false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar2);
        mProgressBar.setVisibility(View.VISIBLE);
        vodService = new VodService();
        userVo = Util.getUserVo(getContext());

        new RefreshCaptureAsyncTask().execute();
        return rootView;
    }

    //db에 캡처이미지 저장
    private class RefreshCaptureAsyncTask extends SafeAsyncTask<Integer> {
        @Override
        public Integer call() throws Exception {
            return vodService.refreshGallery(userVo);
        }

        @Override
        protected void onSuccess(Integer integer) throws Exception {
            if (integer == 200) {
                new GalleryAsyncTask().execute();
            }
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
        }
    }

    //갤러리 뿌리기
    private class GalleryAsyncTask extends SafeAsyncTask<List<CaptureVo>> {
        @Override
        public List<CaptureVo> call() throws Exception {
            return vodService.fetchCapture(userVo);
        }

        @Override
        protected void onSuccess(List<CaptureVo> captureVos) throws Exception {
            infos = new ArrayList<>();
            for (CaptureVo vo : captureVos) {
                infos.add(MediaInfo.mediaLoader(new PicassoImageLoader(URL + userVo.getUsers_no() + "/" + vo.getSave_name())));
            }
            mProgressBar.setVisibility(View.GONE);
            Util.changeHomeFragment(getFragmentManager(), new GalleryFragment(infos));
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
        }
    }

}
