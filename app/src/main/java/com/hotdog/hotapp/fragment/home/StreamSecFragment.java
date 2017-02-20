package com.hotdog.hotapp.fragment.home;

import android.content.SharedPreferences;
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
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.PiVo;
import com.hotdog.hotapp.vo.UserVo;

import cn.refactor.lib.colordialog.PromptDialog;

public class StreamSecFragment extends Fragment {
    private View view;
    private EditText editTextPassword;
    private TextView textIpAddress, secPassErr, secPassErr1;
    private Button buttonSeclogin;
    private UserService userService;
    private UserVo userVo;
    private PiVo piVo;
    private int secpass;
    private SharedPreferences wifiChk;

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
        piVo = Util.getPiVo("piData", getActivity());
        textIpAddress.setText(piVo.getDevice_num());


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
            wifiChk = getActivity().getSharedPreferences("wifiChk", 0);
            int wifi = Util.getConnectivityStatus(getActivity());

            if ("yes".equals(flag)) {
                if (wifi != 1 && wifiChk.getBoolean("chk", false)) {
                    new PromptDialog(getActivity())
                            .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                            .setAnimationEnable(true)
                            .setTitleText("info")
                            .setContentText("wifi 상태가 아닙니다. \n  데이터를 사용하실려면 Settings에서 변경하세요.")
                            .setPositiveListener("확인", new PromptDialog.OnPositiveListener() {
                                @Override
                                public void onClick(PromptDialog dialog) {
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    Util.changeHomeFragment(getFragmentManager(), new StreamSelectFragment());
                }
            } else if ("no".equals(flag)) {
                secPassErr.setVisibility(view.VISIBLE);
            }
        }
    }

}