package com.hotdog.hotapp.fragment.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.extraFragment.BaseFragment;
import com.hotdog.hotapp.other.Util;

public class MypageMainFragment extends BaseFragment {
    private Button buttonUpdateUser, buttonUpdatePet, buttonUpdateDevice, buttonLogout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage_main, container, false);

        buttonUpdateUser = (Button) rootView.findViewById(R.id.buttonUpdateUser);
        buttonUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.changeHomeFragment(getFragmentManager(), new MypageProfileFragment());
            }
        });

        buttonUpdatePet = (Button) rootView.findViewById(R.id.buttonUpdatePet);
        buttonUpdatePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.changeHomeFragment(getFragmentManager(), new MypagePetFragment());
            }
        });

        buttonUpdateDevice = (Button) rootView.findViewById(R.id.buttonUpdateDevice);
        buttonUpdateDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.changeHomeFragment(getFragmentManager(), new MypageRaspLoginFragment());
            }
        });

        buttonLogout = (Button) rootView.findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃
            }
        });

        return rootView;
    }

}
