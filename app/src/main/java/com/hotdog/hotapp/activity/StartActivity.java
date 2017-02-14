package com.hotdog.hotapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.fragment.start.AppStartScreenFragment;
import com.hotdog.hotapp.fragment.start.AppTuto1Fragment;
import com.hotdog.hotapp.fragment.start.AppTuto2Fragment;
import com.hotdog.hotapp.fragment.start.AppTuto3Fragment;

public class StartActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private SharedPreferences.Editor editor;
    private SharedPreferences repeatShow;
    private CheckBox checkBox;
    private Button buttonStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        checkBox = (CheckBox) findViewById(R.id.checkBoxRepeat);
        buttonStart = (Button) findViewById(R.id.buttonStart);

        // 앱 실행 화면 띄우기
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new AppStartScreenFragment()).commit();
        }

        repeatShow = getSharedPreferences("repeatShow", 0);
        editor = repeatShow.edit();
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("chk", true);
                    editor.commit();
                } else {
                    editor.putBoolean("chk", false);
                    editor.commit();
                }
            }
        });


        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // 소개 화면 듀토리얼
        Handler mHandler = new Handler();
        if (repeatShow.getBoolean("chk", false)) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            FrameLayout layout = (FrameLayout) findViewById(R.id.content);
            layout.setVisibility(View.GONE);

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }


    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new AppTuto1Fragment();
                case 1:
                    return new AppTuto2Fragment();
                case 2:
                    return new AppTuto3Fragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}