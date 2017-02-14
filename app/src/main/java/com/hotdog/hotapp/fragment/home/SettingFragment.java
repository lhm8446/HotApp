package com.hotdog.hotapp.fragment.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.PiService;
import com.hotdog.hotapp.vo.PiVo;

public class SettingFragment extends Fragment {

    private static final String TAG = "SettingFragment";
    private PiService piService;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);

        Switch switch1 = (Switch) rootView.findViewById(R.id.switch1);
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Switch switch2 = (Switch) rootView.findViewById(R.id.switch2);
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Token db에 저장
                    String token = FirebaseInstanceId.getInstance().getToken();
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, msg);
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button button = (Button) rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return rootView;
    }

    //pi token 변경
    private class PiTokenUpdateAsyncTask extends SafeAsyncTask<String> {

        @Override
        public String call() throws Exception {
            piService = new PiService();
            PiVo piVo = new PiVo();
            //piVo.setUsers_no();
            piVo.setToken(FirebaseInstanceId.getInstance().getToken());
            return piService.piTokenUpdate(piVo);
        }

        @Override
        protected void onSuccess(String s) throws Exception {

        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }
    }
}
