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
import com.hotdog.hotapp.service.PiService;
import com.hotdog.hotapp.vo.PiVo;

public class MypageRaspChangeRaspFragment extends Fragment {

    private EditText deviceChange;
    private TextView deviceChangeErr;
    private Button deviceChangeBut;
    private PiVo piVo;
    private PiService piService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage_rasp_change_rasp, container, false);
        deviceChange = (EditText) rootView.findViewById(R.id.deviceChange);
        deviceChangeErr = (TextView) rootView.findViewById(R.id.deviceChangeErr);
        deviceChangeBut = (Button) rootView.findViewById(R.id.deviceChangeBut);
        piVo = Util.getPiVo("piData", getActivity());

        deviceChange.setText(piVo.getDevice_num());

        deviceChangeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deviceChange.getText().toString().length() < 4) {
                    deviceChangeErr.setVisibility(view.VISIBLE);
                    return;
                } else {
                    deviceChangeErr.setVisibility(view.GONE);
                    piVo.setDevice_num(deviceChange.getText().toString());
                    new PiDevUpdateAsyncTask().execute();
                }
            }
        });

        return rootView;
    }

    //pi 정보 입력
    private class PiDevUpdateAsyncTask extends SafeAsyncTask<String> {

        @Override
        public String call() throws Exception {
            piService = new PiService();
            return piService.piDevUpdate(piVo);
        }

        @Override
        protected void onSuccess(String s) throws Exception {
            System.out.println("========================" + s);
            if ("success".equals(s)) {
                Toast.makeText(getActivity(), "변경 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
                intent.putExtra("userNo", Util.getUserVo("userData", getActivity()).getUsers_no());
                intent.putExtra("callback", "mypage");
                startActivity(intent);
                getActivity().finish();
            }
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }
    }
}
