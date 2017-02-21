package com.hotdog.hotapp.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.activity.VideoActivity;
import com.hotdog.hotapp.activity.VideoActivity2;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.vo.PiVo;

import cn.refactor.lib.colordialog.PromptDialog;

public class StreamSelectFragment extends Fragment {

    private Button streamMobile, streamRasp;
    private PiVo piVo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stream_select, container, false);

        piVo = Util.getPiVo(getActivity());

        streamMobile = (Button) rootView.findViewById(R.id.streamMobile);
        streamRasp = (Button) rootView.findViewById(R.id.streamRasp);


        streamMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (piVo.getSec_token() != null) {
                    if (!"".equals(piVo.getSec_token())) {
                        startActivity(new Intent(getActivity(), VideoActivity.class));
                    } else {
                        new PromptDialog(getActivity())
                                .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                                .setAnimationEnable(true)
                                .setTitleText("info")
                                .setContentText("준비된 모바일 기기가 없습니다. \n\t Setting에서 확인하세요.")
                                .setPositiveListener("확인", new PromptDialog.OnPositiveListener() {
                                    @Override
                                    public void onClick(PromptDialog dialog) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                } else {
                    new PromptDialog(getActivity())
                            .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                            .setAnimationEnable(true)
                            .setTitleText("info")
                            .setContentText("준비된 모바일 기기가 없습니다. \n\t Setting에서 확인하세요.")
                            .setPositiveListener("확인", new PromptDialog.OnPositiveListener() {
                                @Override
                                public void onClick(PromptDialog dialog) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        });

        streamRasp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (piVo.getDevice_num() != null) {
                    if (!"".equals(piVo.getDevice_num())) {
                        startActivity(new Intent(getActivity(), VideoActivity2.class));
                    } else {
                        new PromptDialog(getActivity())
                                .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                                .setAnimationEnable(true)
                                .setTitleText("info")
                                .setContentText("준비된 라즈베리파이가 없습니다.\n\t Mypage에서 확인하세요.")
                                .setPositiveListener("확인", new PromptDialog.OnPositiveListener() {
                                    @Override
                                    public void onClick(PromptDialog dialog) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                } else {
                    new PromptDialog(getActivity())
                            .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                            .setAnimationEnable(true)
                            .setTitleText("info")
                            .setContentText("준비된 라즈베리파이가 없습니다.\n\t Mypage에서 확인하세요.")
                            .setPositiveListener("확인", new PromptDialog.OnPositiveListener() {
                                @Override
                                public void onClick(PromptDialog dialog) {
                                    dialog.dismiss();
                                }
                            }).show();
                }

            }
        });

        return rootView;
    }


}
