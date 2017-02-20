package com.hotdog.hotapp.fragment.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.Util;

public class StreamSelectFragment extends Fragment {

    private Button streamMobile, streamRasp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stream_select, container, false);


        streamMobile = (Button) rootView.findViewById(R.id.streamMobile);
        streamRasp = (Button) rootView.findViewById(R.id.streamRasp);


        streamMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.changeHomeFragment(getFragmentManager(), new StreamVideo2Fragment());
            }
        });

        streamRasp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.changeHomeFragment(getFragmentManager(), new StreamVideoFragment());
            }
        });


        return rootView;
    }


}
