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

import com.google.firebase.iid.FirebaseInstanceId;
import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.PiService;
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.PiVo;
import com.hotdog.hotapp.vo.UserVo;

public class StreamStartFragment extends Fragment {
    private View view;
    private EditText editTextIp, editTextPassword, editTextPasschk;
    private TextView secPassErr, secPassErr1, secIpErr;
    private Button buttonRegister;
    private UserService userService;
    private PiService piService;
    private UserVo userVo;
    private PiVo piVo;
    private String ip_address;
    private int secpass, secpass2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stream_start, container, false);

        buttonRegister = (Button) rootView.findViewById(R.id.buttonRegister);
        editTextIp = (EditText) rootView.findViewById(R.id.editTextIp);
        editTextPassword = (EditText) rootView.findViewById(R.id.editTextPassword);
        editTextPasschk = (EditText) rootView.findViewById(R.id.editTextPasschk);
        secIpErr = (TextView) rootView.findViewById(R.id.secIpErr);
        secPassErr = (TextView) rootView.findViewById(R.id.secPassErr);
        secPassErr1 = (TextView) rootView.findViewById(R.id.secPassErr1);
        userVo = Util.getUserVo(getActivity());


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextIp.getText().toString().length() <= 0) {
                    secIpErr.setVisibility(view.VISIBLE);
                    return;
                } else {
                    ip_address = editTextIp.getText().toString();
                    secIpErr.setVisibility(view.GONE);
                }

                if (editTextPassword.getText().toString().length() <= 3) {
                    secPassErr1.setVisibility(view.VISIBLE);
                    return;
                } else {
                    secpass = Integer.parseInt(editTextPassword.getText().toString());
                    secPassErr1.setVisibility(view.GONE);
                }

                if (editTextPasschk.getText().toString().length() > 3) {
                    secpass2 = Integer.parseInt(editTextPasschk.getText().toString());
                    if (secpass == secpass2) {
                        String token = FirebaseInstanceId.getInstance().getToken();
                        piVo = new PiVo();
                        piVo.setDevice_num(ip_address);
                        piVo.setToken(token);
                        piVo.setUsers_no(userVo.getUsers_no());
                        userVo.setSec_pass_word(secpass);
                        new RegisterSecAsyncTask().execute();

                        Util.changeHomeFragment(getFragmentManager(), new StreamSecFragment());
                        secPassErr.setVisibility(view.GONE);
                    } else {
                        secPassErr.setVisibility(view.VISIBLE);
                        return;
                    }
                } else {
                    secPassErr.setVisibility(view.VISIBLE);
                    return;
                }

            }
        });

        return rootView;
    }


    //2차 비밀번호 등록
    private class RegisterSecAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            // 통신 완료 후 리턴값 저장
            userService = new UserService();
            return userService.registerSecPass(userVo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(String flag) throws Exception {
            new PiInsertAsyncTask().execute();
        }
    }

    //pi 정보 입력
    private class PiInsertAsyncTask extends SafeAsyncTask<String> {

        @Override
        public String call() throws Exception {
            piService = new PiService();
            return piService.piInsert(piVo);
        }

        @Override
        protected void onSuccess(String s) throws Exception {

        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }
    }
}