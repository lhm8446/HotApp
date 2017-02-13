package com.hotdog.hotapp.fragment.join;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.Util;

// 회원가입 이용약관 체크
public class JoinCheckFragment extends Fragment {
    private CheckBox option1, option2;
    private TextView checkError;
    private Button buttonContinue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_join_check, container, false);
        option1 = (CheckBox) rootView.findViewById(R.id.joinChk1);
        option2 = (CheckBox) rootView.findViewById(R.id.joinChk2);
        checkError = (TextView) rootView.findViewById(R.id.checkError);

        buttonContinue = (Button) rootView.findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이용약관 체크 여부 확인 후 리턴
                if (option1.isChecked() && option2.isChecked()) {
                    Util.changeJoinFragment(getFragmentManager(), new JoinEmailFragment());
                } else {
                    checkError.setVisibility(view.VISIBLE);
                }
            }
        });

        return rootView;
    }
}
