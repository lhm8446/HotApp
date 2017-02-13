package com.hotdog.hotapp.fragment.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.Util;


public class FindPwFragment extends Fragment {
    private TextView keyError;
    private Button keySend, buttonFindChk;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_find_pw, container, false);
        keyError = (TextView) rootView.findViewById(R.id.keyError);

        keySend = (Button) rootView.findViewById(R.id.keySend);
        keySend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이메일 전송
            }
        });

        buttonFindChk = (Button) rootView.findViewById(R.id.buttonEmailChk);
        buttonFindChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (keyCheck()) {
                    Util.changeLoginFragment(getFragmentManager(), new ResetPwFragment());
                } else {
                    keyError.setVisibility(view.VISIBLE);
                }
            }
        });

        return rootView;
    }


    public boolean keyCheck() {
        return true;
    }

}
