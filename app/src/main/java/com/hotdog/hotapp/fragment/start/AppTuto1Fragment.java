package com.hotdog.hotapp.fragment.start;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.extraFragment.BaseFragment;

public class AppTuto1Fragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_app_tuto1, container, false);


        return rootView;
    }
}