package com.hotdog.hotapp.fragment.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.PiService;
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.PiVo;
import com.hotdog.hotapp.vo.UserVo;

public class StreamSecFragment extends Fragment {
    private View view;
    private EditText editTextPassword;
    private TextView textIpAddress, secPassErr, secPassErr1;
    private Button buttonSeclogin;
    private UserService userService;
    private UserVo userVo;
    private PiService piService;
    private int secpass;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stream_sec, container, false);

        buttonSeclogin = (Button) rootView.findViewById(R.id.buttonSeclogin);
        textIpAddress = (TextView) rootView.findViewById(R.id.textIpAddress);
        secPassErr = (TextView) rootView.findViewById(R.id.secPassErr);
        secPassErr1 = (TextView) rootView.findViewById(R.id.secPassErr1);
        editTextPassword = (EditText) rootView.findViewById(R.id.editPassword);

        userVo = Util.getUserVo("userData", getActivity());
        new GetPiInfoAsyncTask().execute();


        buttonSeclogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextPassword.getText().toString().length() <= 3) {
                    secPassErr1.setVisibility(view.VISIBLE);
                    return;
                } else {
                    secpass = Integer.parseInt(editTextPassword.getText().toString());
                    secPassErr1.setVisibility(view.GONE);
                }
                new SecLoginAsyncTask().execute();
            }
        });

        return rootView;
    }


    //2차 로그인
    private class SecLoginAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            userService = new UserService();
            userVo.setSec_pass_word(secpass);
            return userService.SecPassLogin(userVo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
            secPassErr.setVisibility(view.VISIBLE);

        }

        @Override
        protected void onSuccess(String flag) throws Exception {

            if ("yes".equals(flag)) {
                Util.changeHomeFragment(getFragmentManager(), new VideoFragment());
            } else if ("no".equals(flag)) {
                secPassErr.setVisibility(view.VISIBLE);
            }
        }
    }

    //pi 정보 받기
    private class GetPiInfoAsyncTask extends SafeAsyncTask<PiVo> {
        @Override
        public PiVo call() throws Exception {
            piService = new PiService();
            return piService.getinfo(userVo.getUsers_no());
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(PiVo piVo) throws Exception {
            Util.setPiVo("piData", getActivity(), piVo);

            textIpAddress.setText(piVo.getIp_address());
        }
    }
}