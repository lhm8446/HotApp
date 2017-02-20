package com.hotdog.hotapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.fragment.join.JoinCheckFragment;
import com.hotdog.hotapp.other.Util;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Util.changeJoinFragment(getSupportFragmentManager(), new JoinCheckFragment());

    }

    @Override
    public void onBackPressed() {
        LinearLayout joinFragment = (LinearLayout) findViewById(R.id.frame_join);

        if (joinFragment != null) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
