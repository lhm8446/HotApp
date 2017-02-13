package com.hotdog.hotapp.fragment.start;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.extraFragment.BaseFragment;

public class AppStartScreenFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_start_screen, container, false);
    }

}
