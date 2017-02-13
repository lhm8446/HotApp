package com.hotdog.hotapp.fragment.home;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.extraFragment.BaseFragment;

public class MypageNoRaspFragment extends BaseFragment {
    FragmentManager fragmentManager = getFragmentManager();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage_no_rasp, container, false);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            //Do Something
            @Override
            public void run() {
                startFragment(fragmentManager, MypageMainFragment.class);
            }
        }, 2000); // 1000ms

        return rootView;
    }
}
