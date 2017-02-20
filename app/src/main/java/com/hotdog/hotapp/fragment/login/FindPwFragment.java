package com.hotdog.hotapp.fragment.login;

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
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.UserService;


public class FindPwFragment extends Fragment {
    private TextView keyError, textFindEmail;
    private Button keySend, buttonFindChk;
    private EditText editCode;
    private UserService userService;
    private String code;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_find_pw, container, false);
        keyError = (TextView) rootView.findViewById(R.id.keyError);
        textFindEmail = (TextView) rootView.findViewById(R.id.textFindEmail);
        buttonFindChk = (Button) rootView.findViewById(R.id.buttonEmailChk);
        keySend = (Button) rootView.findViewById(R.id.keySend);
        editCode = (EditText) rootView.findViewById(R.id.editCodeChk);
        userService = new UserService();

        textFindEmail.setText(LoginActivity.presentEm);

        keySend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new sendEmailPWAsyncTask().execute();
            }
        });

        buttonFindChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editCode.getText().toString().length() > 0) {
                    if (editCode.getText().toString().equals(code)) {
                        keyError.setVisibility(view.GONE);
                        Util.changeLoginFragment(getFragmentManager(), new ResetPwFragment());
                    } else {
                        keyError.setVisibility(view.VISIBLE);
                    }
                } else {
                    keyError.setVisibility(view.VISIBLE);
                }
            }
        });

        return rootView;
    }


    // 이메일 코드 체크
    private class sendEmailPWAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {
            System.out.println();
            return userService.sendEmail(LoginActivity.presentEm);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(String flag) throws Exception {
            code = flag;
        }
    }
}
