package com.hotdog.hotapp.fragment.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.iid.FirebaseInstanceId;
import com.hotdog.hotapp.R;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.PiService;
import com.hotdog.hotapp.vo.PiVo;
import com.hotdog.hotapp.vo.UserVo;

public class SettingFragment extends Fragment {

    private static final String TAG = "SettingFragment";
    private PiService piService;
    private Switch switch1, switch2;
    private Button buttonHelp;
    private UserVo userVo;
    private PiVo piVo;
    private SharedPreferences.Editor editor;
    private SharedPreferences wifiChk;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);

        switch1 = (Switch) rootView.findViewById(R.id.switch1);
        switch2 = (Switch) rootView.findViewById(R.id.switch2);
        buttonHelp = (Button) rootView.findViewById(R.id.buttonHelp);

        wifiChk = getActivity().getSharedPreferences("wifiChk", 0);
        editor = wifiChk.edit();

        userVo = Util.getUserVo("userData", getActivity());
        piVo = new PiVo();
        piVo.setUsers_no(userVo.getUsers_no());
        switch1.setChecked(wifiChk.getBoolean("chk", false));
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    piVo.setToken(FirebaseInstanceId.getInstance().getToken());
                    new PiTokenUpdateAsyncTask().execute();
                } else {
                    piVo.setToken("");
                    new PiTokenUpdateAsyncTask().execute();
                }
            }
        });
        buttonHelp.setOnClickListener(new View.OnClickListener() {
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
