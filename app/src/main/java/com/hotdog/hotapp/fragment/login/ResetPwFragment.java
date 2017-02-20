package com.hotdog.hotapp.fragment.login;

import android.content.Intent;
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
import com.hotdog.hotapp.activity.LoginActivity;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.UserVo;

public class ResetPwFragment extends Fragment {

    private EditText resetPass, resetPassChk;
    private TextView resetPassErr, resetPassErr1;
    private Button buttonResetPass;
    private String passwordNew;
    private UserVo userVo;
    private UserService userService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_reset_pw, container, false);
        resetPass = (EditText) rootView.findViewById(R.id.resetPass);
        resetPassChk = (EditText) rootView.findViewById(R.id.resetPassChk);
        resetPassErr = (TextView) rootView.findViewById(R.id.resetPassErr);
        resetPassErr1 = (TextView) rootView.findViewById(R.id.resetPassErr1);
        userVo = new UserVo();
        buttonResetPass = (Button) rootView.findViewById(R.id.buttonResetPass);
        buttonResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resetPass.getText().toString().length() > 0) {
                    resetPassErr.setVisibility(view.GONE);

                    passwordNew = resetPass.getText().toString();
                    if (passwordNew.length() < 8) {
                        resetPassErr.setVisibility(view.VISIBLE);
                        return;
                    } else {
                        resetPassErr.setVisibility(view.GONE);
                    }

                    if (resetPassChk.getText().toString().length() >= 8) {
                        if (passwordNew.equals(resetPassChk.getText().toString())) {
                            resetPassErr1.setVisibility(view.GONE);
                            userVo.setEmail(LoginActivity.presentEm);
                            userVo.setPass_word(passwordNew);
                            new PassModifyAsyncTask().execute();
                        } else {
                            resetPassErr1.setVisibility(view.VISIBLE);
                            return;
                        }
                    } else {
                        resetPassErr1.setVisibility(view.VISIBLE);
                        return;
                    }
                } else {
                    resetPassErr.setVisibility(view.VISIBLE);
                    return;
                }
            }
        });
        return rootView;
    }

    // 닉네임 체크
    private class PassModifyAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {
            userService = new UserService();
            return userService.passModify(userVo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(String flag) throws Exception {

            if ("success".equals(flag)) {
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }
    }
}
