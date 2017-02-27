package com.hotdog.hotapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hotdog.hotapp.R;


public class AboutUsActivity extends AppCompatActivity {
    private ImageView userImage1, userImage2, userImage3, userImage4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        userImage1 = (ImageView) findViewById(R.id.userImage1);
        userImage2 = (ImageView) findViewById(R.id.userImage2);
        userImage3 = (ImageView) findViewById(R.id.userImage3);
        userImage4 = (ImageView) findViewById(R.id.userImage4);

        // Loading profile image
        Glide.with(this).load("http://150.95.141.66/hotdog/assets/img/yada.jpg")
                .asBitmap()
                .into(userImage1);  // Loading profile image
        Glide.with(this).load("http://150.95.141.66/hotdog/assets/img/leehamin.jpg")
                .asBitmap()
                .into(userImage2);
        // Loading profile image
        Glide.with(this).load("http://150.95.141.66/hotdog/assets/img/bjw.jpg")
                .asBitmap()
                .into(userImage3);
        // Loading profile image
        Glide.with(this).load("http://150.95.141.66/hotdog/assets/img/jw.jpg")
                .asBitmap()
                .into(userImage4);


    }

}
