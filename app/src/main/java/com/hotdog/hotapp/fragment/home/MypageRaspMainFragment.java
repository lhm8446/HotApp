package com.hotdog.hotapp.fragment.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hotdog.hotapp.R;

public class MypageRaspMainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage_rasp_main, container, false);

        Button button1 = (Button)rootView.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeRaspChangePwFragment();
            }
        });

        Button button2 = (Button)rootView.findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeRaspFragment();
            }
        });

        Button button3 = (Button)rootView.findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return rootView;
    }

    private void changeRaspChangePwFragment() {
        getFragmentManager().beginTransaction().replace(R.id.frame, new MypageRaspChangePwFragment()).addToBackStack(null).commit();
    }
    private void changeRaspFragment() {
        getFragmentManager().beginTransaction().replace(R.id.frame, new MypageRaspChangeRaspFragment()).addToBackStack(null).commit();
    }
}
