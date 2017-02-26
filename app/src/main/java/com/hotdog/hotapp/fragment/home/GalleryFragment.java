package com.hotdog.hotapp.fragment.home;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.activity.VideoActivity;
import com.hotdog.hotapp.other.PicassoImageLoader;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.VodService;
import com.hotdog.hotapp.vo.CaptureVo;
import com.hotdog.hotapp.vo.UserVo;
import com.veinhorn.scrollgalleryview.MediaInfo;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;
import com.veinhorn.scrollgalleryview.loader.DefaultImageLoader;
import com.veinhorn.scrollgalleryview.loader.DefaultVideoLoader;
import com.veinhorn.scrollgalleryview.loader.MediaLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {

    private ScrollGalleryView scrollGalleryView;
    private List<MediaInfo> infos;

    public GalleryFragment(List<MediaInfo> infos) {
        this.infos = infos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_gallery, container, false);
        scrollGalleryView = (ScrollGalleryView) rootView.findViewById(R.id.scroll_gallery_view);

        scrollGalleryView
                .setThumbnailSize(100)
                .setZoom(true)
                .setFragmentManager(getFragmentManager())
                .addMedia(infos);
        return rootView;
    }

    @Override
    public void onDestroy() {
        getFragmentManager().popBackStack();
        super.onDestroy();
    }
}
