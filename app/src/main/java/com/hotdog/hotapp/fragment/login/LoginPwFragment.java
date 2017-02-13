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
import com.hotdog.hotapp.activity.HomeActivity;
import com.hotdog.hotapp.activity.JoinActivity;
import com.hotdog.hotapp.activity.LoginActivity;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.UserVo;

// (로그인) 이메일 + 비밀번호 프래그먼트
public class LoginPwFragment extends Fragment {
    private TextView pwError;
    private EditText editPassword;
    private String password;
    private View view;
    private Button login, find, singUp;
    private UserService userService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_login_pw, container, false);
        pwError = (TextView) rootView.findViewById(R.id.pwError);
        editPassword = (EditText) rootView.findViewById(R.id.editPassword);

        login = (Button) rootView.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력된 비밀번호 스트링값 저장
                password = editPassword.getText().toString();

                // 통신(이메일 + 비밀번호 로그인 체크)
                new FetchUserEmailPasswordAsyncTask().execute();
            }
        });

        find = (Button) rootView.findViewById(R.id.find);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.changeLoginFragment(getFragmentManager(), new FindPwFragment());
            }
        });

        // 회원가입 이동
        singUp = (Button) rootView.findViewById(R.id.signUp);
        singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private class FetchUserEmailPasswordAsyncTask extends SafeAsyncTask<UserVo> {
        @Override
        public UserVo call() throws Exception {

            // 통신 완료 후 리턴값 저장
            userService = new UserService();
            UserVo userVo = userService.userEmailPasswordCheck(LoginActivity.presentEm, password);

            return userVo;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(UserVo userVo) throws Exception {

            // 로그인 안되면 '-1' 리턴
            if (userVo.getUsers_no() != -1) {
                Intent intent = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
                intent.putExtra("userNo", userVo.getUsers_no());
                startActivity(intent);
                getActivity().finish();
            } else {
                pwError.setVisibility(view.VISIBLE);
            }
        }
    }


}
