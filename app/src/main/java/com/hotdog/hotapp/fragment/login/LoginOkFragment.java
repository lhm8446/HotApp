package com.hotdog.hotapp.fragment.login;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.extraFragment.BaseFragment;

public class LoginOkFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_ok, container, false);
    }

}
