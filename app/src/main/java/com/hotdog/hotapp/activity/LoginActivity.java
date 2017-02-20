package com.hotdog.hotapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.fragment.login.LoginEmFragment;
import com.hotdog.hotapp.other.Util;

public class LoginActivity extends AppCompatActivity {
    // 이메일 로그인 시 '최종 로그인' 사용을 위해 저장 ( sharedpreference 구현 x )
    public static String presentEm;


    @Override
    public void onBackPressed() {
        FrameLayout loginFragment = (FrameLayout) findViewById(R.id.frame_login);

        if (loginFragment != null) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Util.changeLoginFragment(getSupportFragmentManager(), new LoginEmFragment());

    }
}
