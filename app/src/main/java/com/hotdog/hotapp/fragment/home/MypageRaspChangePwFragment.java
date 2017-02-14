package com.hotdog.hotapp.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.activity.HomeActivity;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.UserVo;

public class MypageRaspChangePwFragment extends Fragment {

    private EditText editSecPass, editSecPassChk;
    private Button secChange;
    private TextView secChangeErr, secChangeErr1;
    private int secPass, secPass2;
    private UserVo userVo;
    private UserService userService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage_rasp_change_pw, container, false);
        editSecPass = (EditText) rootView.findViewById(R.id.editSecPass);
        editSecPassChk = (EditText) rootView.findViewById(R.id.editSecPassChk);
        secChange = (Button) rootView.findViewById(R.id.secChange);
        secChangeErr = (TextView) rootView.findViewById(R.id.secChangeErr);
        secChangeErr1 = (TextView) rootView.findViewById(R.id.secChangeErr1);

        userVo = Util.getUserVo("userData", getActivity());
        secChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editSecPass.getText().toString().length() <= 3) {
                    secChangeErr.setVisibility(view.VISIBLE);
                    return;
                } else {
                    secPass = Integer.parseInt(editSecPass.getText().toString());
                    secChangeErr.setVisibility(view.GONE);
                }
                if (editSecPassChk.getText().toString().length() > 3) {
                    secPass2 = Integer.parseInt(editSecPassChk.getText().toString());
                    if (secPass == secPass2) {

                        userVo.setSec_pass_word(secPass);
                        new ModifySecAsyncTask().execute();
                        secChangeErr1.setVisibility(view.GONE);
                    } else {
                        secChangeErr1.setVisibility(view.VISIBLE);
                        return;
                    }
                } else {
                    secChangeErr1.setVisibility(view.VISIBLE);
                    return;
                }
            }
        });

        return rootView;
    }

    private class ModifySecAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            // 통신 완료 후 리턴값 저장
            userService = new UserService();
            return userService.registerSecPass(userVo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(String flag) throws Exception {
            Toast.makeText(getActivity(), "변경 되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
            intent.putExtra("userNo", userVo.getUsers_no());
            intent.putExtra("callback", "mypage");
            startActivity(intent);
            getActivity().finish();
        }
    }
}
