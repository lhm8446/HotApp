package com.hotdog.hotapp.fragment.join;

import android.content.Intent;
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
import com.hotdog.hotapp.activity.LoginActivity;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.UserVo;

// 회원가입 개인정보 입력
public class JoinInfoFragment extends Fragment {
    private EditText nicknameText, pass_wordText, pass_word2Text;
    private TextView nicknameEr, nicknameEr2, pass_wordEr, pass_word2Er, textviewEmail;
    private String email, nickname, pass_word, pass_word2;
    private View view;
    private UserService userService;
    private Boolean joinCheck;
    private Button button;
    private SharedPreferences emailShared;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_join_info, container, false);

        nicknameText = (EditText) rootView.findViewById(R.id.nickname);
        pass_wordText = (EditText) rootView.findViewById(R.id.pass_word);
        pass_word2Text = (EditText) rootView.findViewById(R.id.pass_word2);
        textviewEmail = (TextView) rootView.findViewById(R.id.textviewEmail);
        nicknameEr = (TextView) rootView.findViewById(R.id.nicknameEr);
        nicknameEr2 = (TextView) rootView.findViewById(R.id.nicknameEr2);
        pass_wordEr = (TextView) rootView.findViewById(R.id.pass_wordEr);
        pass_word2Er = (TextView) rootView.findViewById(R.id.pass_word2Er);

        emailShared = getActivity().getSharedPreferences("email", 0);
        email = emailShared.getString("email", "none");
        textviewEmail.setText(email);

        userService = new UserService();

        nicknameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // 닉네임 체크
                if (!b) {
                    nickname = nicknameText.getText().toString();
                    if (nickname.length() < 2) {
                        nicknameEr2.setVisibility(view.VISIBLE);
                        return;
                    } else {
                        nicknameEr2.setVisibility(view.GONE);
                    }

                    new UserNicknameChkAsyncTask().execute();
                }
            }
        });

        pass_wordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // 비밀번호 형식 체크
                if (!b) {
                    pass_word = pass_wordText.getText().toString();

                    if (pass_word.length() < 7) {
                        pass_wordEr.setVisibility(view.VISIBLE);
                        return;
                    } else {
                        pass_wordEr.setVisibility(view.GONE);
                    }
                }
            }
        });

        pass_word2Text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (pass_wordText.getText().toString().equals(pass_word2Text.getText().toString())) {
                        pass_word2Er.setVisibility(view.GONE);
                    } else {
                        pass_word2Er.setVisibility(view.VISIBLE);
                    }
                }
            }
        });


        button = (Button) rootView.findViewById(R.id.buttonJoin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinCheck = true;
                // 닉네임 체크
                nickname = nicknameText.getText().toString();
                if (nickname.length() < 2) {
                    nicknameEr2.setVisibility(view.VISIBLE);
                    return;
                } else {
                    nicknameEr2.setVisibility(view.GONE);
                }
                new UserNicknameChkAsyncTask().execute();


                // 비번 체크
                pass_word = pass_wordText.getText().toString();
                if (pass_word.length() < 7) {
                    pass_wordEr.setVisibility(view.VISIBLE);
                    return;
                } else {
                    pass_wordEr.setVisibility(view.GONE);
                }

                pass_word2 = pass_word2Text.getText().toString();
                if (pass_wordText.getText().toString().equals(pass_word2Text.getText().toString())) {
                    pass_word2Er.setVisibility(view.GONE);
                } else {
                    pass_word2Er.setVisibility(view.VISIBLE);
                    return;
                }

                if (joinCheck) {
                    // 회원가입 체크 통신
                    new FetchUserInsertAsyncTask().execute();
                }

            }
        });

        return rootView;
    }

    // 닉네임 체크
    private class UserNicknameChkAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            return userService.userNickCheck(nickname);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(String flag) throws Exception {

            // 닉네임 존재하면 'no' 아니면 'yes' 리턴
            if ("yes".equals(flag)) {
                nicknameEr.setVisibility(view.GONE);
                joinCheck = true;
            } else {
                nicknameEr.setVisibility(view.VISIBLE);
                joinCheck = false;
            }
        }
    }


    // 회원가입
    private class FetchUserInsertAsyncTask extends SafeAsyncTask<UserVo> {
        @Override
        public UserVo call() throws Exception {

            // 통신 할 값 vo 저장
            UserVo userVo = new UserVo();
            userVo.setEmail(email);
            userVo.setPass_word(pass_word);
            userVo.setNickname(nickname);

            return userService.userInsert(userVo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(UserVo userVo) throws Exception {

            // 통신 완료 값은 사용자 번호. 0보다 크면 회원가입 완료 아니면 실패
            // 회원가입 완료 + 회원가입 실패 프래그먼트 띄우기 ( 구현 x )
            if (userVo.getUsers_no() > 0) {
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }
    }

}