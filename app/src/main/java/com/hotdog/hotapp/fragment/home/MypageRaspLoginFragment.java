package com.hotdog.hotapp.fragment.home;

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
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.UserVo;

public class MypageRaspLoginFragment extends Fragment {
    private View view;

    private Button buttonSec;
    private EditText secEdit;
    private int secPass;
    private UserVo userVo;
    private UserService userService;
    private TextView secTextErr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage_rasp_login, container, false);

        buttonSec = (Button) rootView.findViewById(R.id.buttonSec);
        secEdit = (EditText) rootView.findViewById(R.id.secEdit);
        secTextErr = (TextView) rootView.findViewById(R.id.secTextErr);

        userVo = Util.getUserVo("userData", getActivity());
        buttonSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (secEdit.getText().toString().length() > 0) {
                    secPass = Integer.parseInt(secEdit.getText().toString());
                    new SecLoginAsyncTask().execute();
                } else {
                    return;
                }
            }
        });

        return rootView;
    }

    //2차 로그인
    private class SecLoginAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            userService = new UserService();
            userVo.setSec_pass_word(secPass);
            return userService.SecPassLogin(userVo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
            secTextErr.setVisibility(view.VISIBLE);

        }

        @Override
        protected void onSuccess(String flag) throws Exception {

            if ("yes".equals(flag)) {
                Util.changeHomeFragment(getFragmentManager(), new MypageRaspMainFragment());
            } else if ("no".equals(flag)) {
                secTextErr.setVisibility(view.VISIBLE);
            }
        }
    }
}
