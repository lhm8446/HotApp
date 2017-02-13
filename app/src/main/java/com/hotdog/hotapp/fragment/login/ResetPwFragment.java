package com.hotdog.hotapp.fragment.login;

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
import com.hotdog.hotapp.other.Util;

public class ResetPwFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_reset_pw, container, false);
        final EditText editText = (EditText) rootView.findViewById(R.id.editPassword);
        final EditText edittex2 = (EditText) rootView.findViewById(R.id.edittex2);
        final TextView textView = (TextView) rootView.findViewById(R.id.textView);
        final TextView textView2 = (TextView) rootView.findViewById(R.id.textView2);

        Button button = (Button) rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pwType(editText.getText().toString())) {

                    if (samePw(editText.getText().toString(), edittex2.getText().toString())) {
                        changePwData();
                        Util.changeLoginFragment(getFragmentManager(), new LoginEmFragment());
                    } else {
                        textView2.setVisibility(view.VISIBLE);
                    }

                } else {
                    textView.setVisibility(view.VISIBLE);
                }

            }
        });

        return rootView;
    }

    public boolean pwType(String text) {

        return true;
    }

    public boolean samePw(String text, String text2) {
        if (text.equals("text2")) {
            return true;
        } else {
            return false;
        }
    }

    public void changePwData() {

    }

}
