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
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.service.UserService;


public class JoinEmailFragment extends Fragment {
    private EditText editEmailCHk, editPassword;
    private TextView emailChkEr, emailChkEr2, keyError;
    private String email;
    private View view;
    private UserService userService;
    private Button keyEmailSend, buttonEmailChk;
    private boolean flag;
    private SharedPreferences baseSetting;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_join_email, container, false);

        editEmailCHk = (EditText) rootView.findViewById(R.id.editEmailCHk);
        editPassword = (EditText) rootView.findViewById(R.id.editPassword);
        keyError = (TextView) rootView.findViewById(R.id.keyError);
        emailChkEr = (TextView) rootView.findViewById(R.id.emailChkEr);
        emailChkEr2 = (TextView) rootView.findViewById(R.id.emailChkEr2);
        userService = new UserService();
        baseSetting = getActivity().getSharedPreferences("email", 0);
        editor = baseSetting.edit();

        editEmailCHk.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // 이메일 체크
                if (!b) {
                    email = editEmailCHk.getText().toString();

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailChkEr2.setVisibility(view.VISIBLE);
                        return;
                    } else {
                        emailChkEr2.setVisibility(view.GONE);
                    }

                    new UserEmailChkAsyncTask().execute();
                }
            }
        });


        buttonEmailChk = (Button) rootView.findViewById(R.id.buttonEmailChk);
        buttonEmailChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = true;
                // 이메일 체크
                email = editEmailCHk.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailChkEr2.setVisibility(view.VISIBLE);
                    return;
                } else {
                    emailChkEr2.setVisibility(view.GONE);
                }
                new UserEmailChkAsyncTask().execute();

                if (flag) {
                    editor.putString("email", email);
                    editor.commit();
                    Util.changeJoinFragment(getFragmentManager(), new JoinInfoFragment());
                }


            }
        });

        return rootView;
    }


    // 이메일 체크
    private class UserEmailChkAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            return userService.userEmailCheck(email);
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
                flag = false;
            } else {
                emailChkEr.setVisibility(view.GONE);
                flag = true;
            }
        }
    }


}