package com.hotdog.hotapp.fragment.join;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
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


public class JoinEmailFragment extends Fragment {
    private EditText editEmailCHk, editCode;
    private TextView emailChkEr, emailChkEr2, keyError;
    private String email, sendEmail;
    private View view;
    private UserService userService;
    private Button keyEmailSend, buttonEmailChk;
    private SharedPreferences baseSetting;
    private SharedPreferences.Editor editor;
    private String code;
    private boolean flag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_join_email, container, false);

        editEmailCHk = (EditText) rootView.findViewById(R.id.editEmailCHk);
        editCode = (EditText) rootView.findViewById(R.id.editCode);
        keyError = (TextView) rootView.findViewById(R.id.keyError);
        emailChkEr = (TextView) rootView.findViewById(R.id.emailChkEr);
        emailChkEr2 = (TextView) rootView.findViewById(R.id.emailChkEr2);
        keyEmailSend = (Button) rootView.findViewById(R.id.keyEmailSend);
        buttonEmailChk = (Button) rootView.findViewById(R.id.buttonEmailChk);

        userService = new UserService();
        baseSetting = getActivity().getSharedPreferences("email", 0);
        editor = baseSetting.edit();

        keyEmailSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail = editEmailCHk.getText().toString();
                flag = false;
                new UserEmailChkAsyncTask().execute();

            }
        });
        buttonEmailChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = true;
                email = editEmailCHk.getText().toString();
                if (email.equals(sendEmail)) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailChkEr2.setVisibility(view.VISIBLE);
                        return;
                    } else {
                        emailChkEr2.setVisibility(view.GONE);
                    }

                    if (editCode.getText().toString().length() > 0) {
                        if (editCode.getText().toString().equals(code)) {
                            keyError.setVisibility(view.GONE);
                            new UserEmailChkAsyncTask().execute();
                        } else {
                            keyError.setVisibility(view.VISIBLE);
                        }
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


    // 이메일 체크
    private class UserEmailChkAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {
            if (flag) {
                return userService.userEmailCheck(email);
            } else {
                return userService.userEmailCheck(sendEmail);
            }
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(String chk) throws Exception {
            // 이메일 존재하면 'exist' 아니면 'not exist' 리턴
            if ("exist".equals(chk)) {
                emailChkEr.setVisibility(view.VISIBLE);
            } else {
                if (flag) {
                    flag = false;
                    code = "";
                    editor.putString("email", email);
                    editor.commit();
                    Util.changeJoinFragment(getFragmentManager(), new JoinInfoFragment());
                } else {
                    new sendEmailAsyncTask().execute();
                }
            }

        }
    }

    // 이메일 코드 체크
    private class sendEmailAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            return userService.sendEmail(sendEmail);
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