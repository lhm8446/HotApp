package com.hotdog.hotapp.fragment.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hotdog.hotapp.R;

public class StreamingFragment extends Fragment {
    SharedPreferences baseSetting;
    SharedPreferences.Editor editor;
    EditText editTextDevice, editTextPassword, editTextPasschk;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_streaming, container, false);

        Button buttonRegister = (Button) rootView.findViewById(R.id.buttonRegister);
        editTextDevice = (EditText) rootView.findViewById(R.id.editTextDevice);
        editTextPassword = (EditText) rootView.findViewById(R.id.editTextPassword);
        editTextPasschk = (EditText) rootView.findViewById(R.id.editTextPasschk);

        baseSetting = this.getActivity().getSharedPreferences("setting", 0);
        editor = baseSetting.edit();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int secpass = Integer.parseInt(editTextPassword.getText().toString());
                int secpass2 = Integer.parseInt(editTextPasschk.getText().toString());

                if (secpass == secpass2) {
                    editor.putInt("secpass", secpass);
                    editor.putString("ipnumber", editTextDevice.getText().toString());
                    editor.commit();
                    changeFragment();
                }

            }
        });

        return rootView;
    }

    private void changeFragment() {
        getFragmentManager().beginTransaction().replace(R.id.frame, new VideoFragment()).addToBackStack(null).commit();
    }
}