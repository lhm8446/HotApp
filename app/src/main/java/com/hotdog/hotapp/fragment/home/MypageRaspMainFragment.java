package com.hotdog.hotapp.fragment.home;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.vo.PiVo;
import com.hotdog.hotapp.vo.UserVo;

public class MypageRaspMainFragment extends Fragment {

    private TextView raspDevice;
    private Button secPassChange, DeviceNumChange, DeviceNumDelete;
    private UserVo userVo;
    private PiVo piVo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage_rasp_main, container, false);
        System.out.println(getFragmentManager().getBackStackEntryCount());
        raspDevice = (TextView) rootView.findViewById(R.id.raspDevice);

        secPassChange = (Button) rootView.findViewById(R.id.secPassChange);
        DeviceNumChange = (Button) rootView.findViewById(R.id.DeviceNumChange);
        DeviceNumDelete = (Button) rootView.findViewById(R.id.DeviceNumDelete);

        userVo = Util.getUserVo(getActivity());
        piVo = Util.getPiVo(getActivity());

        raspDevice.setText(piVo.getDevice_num());

        secPassChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.changeHomeFragment(getFragmentManager(), new MypageRaspChangePwFragment());
            }
        });

        DeviceNumChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.changeHomeFragment(getFragmentManager(), new MypageRaspChangeRaspFragment());
            }
        });

        DeviceNumDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        getFragmentManager().popBackStack();
        System.out.println(getFragmentManager().getBackStackEntryCount());
        super.onDestroy();
    }
}
