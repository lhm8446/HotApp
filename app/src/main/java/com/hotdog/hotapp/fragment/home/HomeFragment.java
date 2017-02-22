package com.hotdog.hotapp.fragment.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.vo.PetVo;
import com.hotdog.hotapp.vo.UserVo;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;

import tyrantgit.widget.HeartLayout;

public class HomeFragment extends Fragment {
    private Random mRandom = new Random();
    private Timer mTimer = new Timer();
    private HeartLayout mHeartLayout;
    private ImageView userImage, petImage;
    private TextView textCount;
    private static final String urlimg = "http://150.95.141.66:80/hotdog/hotdog/image/user/";
    private UserVo userVo;
    private PetVo petVo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        mHeartLayout = (HeartLayout) rootView.findViewById(R.id.heart_layout);
        userImage = (ImageView) rootView.findViewById(R.id.userImage);
        petImage = (ImageView) rootView.findViewById(R.id.petImage);
        textCount = (TextView) rootView.findViewById(R.id.textCount);
        userVo = Util.getUserVo(getActivity());
        petVo = Util.getPetVo(getActivity());
    /*    mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHeartLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mHeartLayout.addHeart(randomColor());
                    }
                });
            }
        }, 500, 200);*/
        init();
        return rootView;
    }

    public void init() {
        String urlProfileImg = urlimg + userVo.getUsers_image();
        String urlPetImg = urlimg + petVo.getPet_image();
        Calendar cal = Calendar.getInstance();
        if (petVo.getCo_date().contains("/")) {
            String[] str = petVo.getCo_date().split("/");
            cal.set(Integer.parseInt(str[2]), Integer.parseInt(str[1]) - 1, Integer.parseInt(str[0]));
        }

        Calendar now = Calendar.getInstance();
        now.getTimeZone();
        int dday = dayCounting(cal, now) > 0 ? dayCounting(cal, now) + 1 : dayCounting(cal, now);
        textCount.setText(dday + "일");

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .asBitmap()
                .into(userImage);

        // Loading profile image
        Glide.with(this).load(urlPetImg)
                .asBitmap()
                .into(petImage);

    }

    public int dayCounting(Calendar now, Calendar dday) {
        int result = 0;

        int nowDayCnt = now.get(Calendar.DAY_OF_YEAR);
        int dDayCnt = dday.get(Calendar.DAY_OF_YEAR);

        int nowYr = now.get(Calendar.YEAR);
        int dDayYr = dday.get(Calendar.YEAR);

        int leapYrCnt = 0;
        int normalYrCnt = 0;

        if ((nowYr == dDayYr) && (dDayCnt - nowDayCnt > 0)) {
            result = dDayCnt - nowDayCnt;
        } else if (dDayYr > nowYr) {
            for (int i = nowYr + 1; i < dDayYr; i++) {
                if ((0 == (i % 4) && 0 != (i % 100)) || 0 == i % 400) {
                    leapYrCnt++;
                } else {
                    normalYrCnt++;
                }
            }
            if (((0 == (nowYr % 4) && 0 != (nowYr % 100)) || 0 == nowYr % 400) && nowDayCnt < 60) {
                result = dDayCnt + (366 - nowDayCnt) + (365 * normalYrCnt) + (366 * leapYrCnt);
            } else {
                result = dDayCnt + (365 - nowDayCnt) + (365 * normalYrCnt) + (366 * leapYrCnt);
            }
        } else {
            for (int i = dDayYr; i < nowYr; i++) {
                if ((0 == (i % 4) && 0 != (i % 100)) || 0 == i % 400) {
                    leapYrCnt++;
                } else {
                    normalYrCnt++;
                }
            }

            result = dDayCnt - nowDayCnt - (365 * normalYrCnt) - (366 * leapYrCnt);
          /*  if (result < 0) {
                result = (result * -1) + 1;
            }*/
        }
        return result;
    }

    private int randomColor() {
        return Color.rgb(mRandom.nextInt(255), 0, 0);
    }


}
