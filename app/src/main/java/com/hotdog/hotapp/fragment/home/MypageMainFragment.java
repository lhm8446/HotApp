package com.hotdog.hotapp.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.activity.LoginActivity;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.extraFragment.BaseFragment;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.UserVo;

public class MypageMainFragment extends BaseFragment {
    private Button buttonUpdateUser, buttonUpdatePet, buttonUpdateDevice, buttonLogout;
    private UserVo userVo;
    private UserService userService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage_main, container, false);
        userVo = Util.getUserVo(getActivity());

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
                new SecPassChkAsyncTask().execute();

            }
        });

        buttonLogout = (Button) rootView.findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.logout(getActivity());
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return rootView;
    }


    //2차 존재 여부 확인
    private class SecPassChkAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            // 통신 완료 후 리턴값 저장
            userService = new UserService();

            return userService.chkSecPass(userVo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(String flag) throws Exception {
            if ("exist".equals(flag)) {
                Util.changeHomeFragment(getFragmentManager(), new MypageRaspLoginFragment());
            } else if ("first".equals(flag)) {
                Util.changeHomeFragment(getFragmentManager(), new StreamStart2Fragment());
            }
        }
    }

}
