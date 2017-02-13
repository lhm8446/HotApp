package com.hotdog.hotapp.fragment.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hotdog.hotapp.R;
import com.hotdog.hotapp.activity.HomeActivity;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.other.CircleTransform;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.service.UploadService;
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.UserVo;

import java.io.File;

public class MypageProfileFragment extends Fragment {

    private View view;
    private UserService userService;
    private Boolean joinCheck;

    private Button search_picture, userUpdate, buttonQuit;

    private SharedPreferences baseSetting;
    private SharedPreferences.Editor editor;
    private UserVo userVo, userVoNew;
    private static final String urlimg = "http://150.95.141.66/hotdog/hotdog/image/user/";
    private UploadService uploadService;
    private String pass_wordNew, nicknameNew;

    private EditText edittext_nickname, edittext_now_pass_word, edittext_change_pass_word, edittext_change_pass_word2;
    private TextView textview_email, textview_nicknameEr, textview_nicknameEr2, textview_now_pass_wordEr, textview_change_pass_wordEr, textview_change_pass_word2Er;
    private ImageView profile_picture;

    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    private final int REQ_CODE_SELECT_IMAGE = 1001;
    private String mImgPath = null;
    private String mImgTitle = null;
    private Bitmap bm;
    private File saveFile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage_profile, container, false);
        userVo = Util.getUserVo("userData", getActivity());


        textview_email = (TextView) rootView.findViewById(R.id.textview_email);
        edittext_nickname = (EditText) rootView.findViewById(R.id.edittext_nickname);
        edittext_now_pass_word = (EditText) rootView.findViewById(R.id.edittext_now_pass_word);
        edittext_change_pass_word = (EditText) rootView.findViewById(R.id.edittext_change_pass_word);
        edittext_change_pass_word2 = (EditText) rootView.findViewById(R.id.edittext_change_pass_word2);

        textview_nicknameEr = (TextView) rootView.findViewById(R.id.edittext_nicknameEr);
        textview_nicknameEr2 = (TextView) rootView.findViewById(R.id.edittext_nicknameEr2);
        textview_now_pass_wordEr = (TextView) rootView.findViewById(R.id.edittext_now_pass_wordEr);
        textview_change_pass_wordEr = (TextView) rootView.findViewById(R.id.edittext_change_pass_wordEr);
        textview_change_pass_word2Er = (TextView) rootView.findViewById(R.id.edittext_change_pass_word2Er);

        userUpdate = (Button) rootView.findViewById(R.id.userUpdate);
        search_picture = (Button) rootView.findViewById(R.id.search_picture);
        buttonQuit = (Button) rootView.findViewById(R.id.buttonQuit);
        profile_picture = (ImageView) rootView.findViewById(R.id.profile_picture);

        textview_email.setText(userVo.getEmail());
        edittext_nickname.setText(userVo.getNickname());
        loadPofile_picture();

        search_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Util.checkStoragePermission(getActivity())) {
                    getGallery();
                }
            }
        });

        edittext_nickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // 닉네임 체크
                if (!b) {
                    if (edittext_nickname.getText().toString().length() < 2) {
                        textview_nicknameEr2.setVisibility(view.VISIBLE);
                        return;
                    } else {
                        textview_nicknameEr2.setVisibility(view.GONE);
                    }

                    new UserNicknameAsyncTask().execute();
                }
            }
        });

        edittext_now_pass_word.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // 현재 비밀번호 확인
                if (!b) {

                    if (userVo.getPass_word().equals(edittext_now_pass_word.getText().toString())) {
                        textview_now_pass_wordEr.setVisibility(view.GONE);
                    } else {
                        textview_now_pass_wordEr.setVisibility(view.VISIBLE);
                        return;
                    }

                }
            }
        });


        userUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinCheck = true;
                nicknameNew = edittext_nickname.getText().toString();
                if (saveFile != null) {
                    new UploadTask(saveFile, userVo).execute();
                }
                // 닉네임 체크
                if (nicknameNew.length() < 2) {
                    textview_nicknameEr2.setVisibility(view.VISIBLE);
                    return;
                } else {
                    textview_nicknameEr2.setVisibility(view.GONE);
                }
                if (!nicknameNew.equals(userVo.getNickname())) {
                    new UserNicknameAsyncTask().execute();
                }
                // 현재 비밀번호 확인
                if (edittext_now_pass_word.getText().toString().length() > 0) {
                    if (userVo.getPass_word().equals(edittext_now_pass_word.getText().toString())) {
                        textview_now_pass_wordEr.setVisibility(view.GONE);
                        //비번체크
                        pass_wordNew = edittext_change_pass_word.getText().toString();
                        if (pass_wordNew.length() < 8) {
                            textview_change_pass_wordEr.setVisibility(view.VISIBLE);
                            return;
                        } else {
                            textview_change_pass_wordEr.setVisibility(view.GONE);
                        }
                        if (pass_wordNew.equals(edittext_change_pass_word2.getText().toString())) {
                            textview_change_pass_word2Er.setVisibility(view.GONE);
                        } else {
                            textview_change_pass_word2Er.setVisibility(view.VISIBLE);
                            return;
                        }
                    } else {
                        textview_now_pass_wordEr.setVisibility(view.VISIBLE);
                        return;
                    }
                } else {
                    pass_wordNew = userVo.getPass_word();
                }

                if (joinCheck) {
                    // 회원가입 체크 통신
                    new UserModifytAsyncTask().execute();
                }
            }
        });


        buttonQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return rootView;
    }

    // 프로필 로딩
    private void loadPofile_picture() {
        String urlProfileImg = urlimg + "/" + userVo.getUsers_image();

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(getActivity().getApplication()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profile_picture);

    }

    // 사진 업로드 하기
    private void getGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.setType("image/*");
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 선택된 사진을 받아 서버에 업로드한다
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                getImageNameToUri(uri);

                try {
                    bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                    saveFile = new File(mImgPath);
                    profile_picture.setImageBitmap(bm);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
            }
        } else {
        }
    }

    private void getImageNameToUri(Uri data) {
        String[] proj = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.ORIENTATION
        };

        Cursor cursor = getActivity().getContentResolver().query(data, proj, null, null, null);
        cursor.moveToFirst();

        int column_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int column_title = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
        mImgPath = cursor.getString(column_data);
        mImgTitle = cursor.getString(column_title);
    }


    // 사진 서버 올리기
    private class UploadTask extends SafeAsyncTask<String> {
        File file;
        UserVo userVo;

        public UploadTask(File file, UserVo userVo) {
            this.file = file;
            this.userVo = userVo;
        }

        @Override
        public String call() throws Exception {

            uploadService = new UploadService();
            return uploadService.uploadUserImage(file, userVo);
        }

        @Override
        protected void onSuccess(String url_image) throws Exception {

            Toast.makeText(getActivity().getApplicationContext(), "사진이 수정됬습니다.", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            System.out.println("-------------------- 사진 서버 에러 ------------------- " + e);
            super.onException(e);
        }
    }

    // 닉네임 체크
    private class UserNicknameAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            // 통신 할 값 리턴값 저장
            userService = new UserService();
            return userService.userNickCheck(nicknameNew);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(String flag) throws Exception {

            // 닉네임 존재하면 'no' 아니면 'yes' 리턴
            if ("yes".equals(flag)) {
                textview_nicknameEr.setVisibility(view.GONE);
                joinCheck = true;
            } else {
                textview_nicknameEr.setVisibility(view.VISIBLE);
                joinCheck = false;
            }
        }
    }

    // 회원 수정 클릭
    private class UserModifytAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            userService = new UserService();
            // 통신 할 값 vo 저장
            userVoNew = new UserVo();
            userVoNew.setUsers_no(userVo.getUsers_no());
            userVoNew.setNickname(nicknameNew);
            userVoNew.setPass_word(pass_wordNew);
            return userService.userModify(userVoNew);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(String flag) throws Exception {
            // 회원 수정
            if ("success".equals(flag)) {
                Intent intent = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
                intent.putExtra("userNo", userVo.getUsers_no());
                intent.putExtra("callback", "mypage");
                startActivity(intent);
                getActivity().finish();
            } else {
            }
        }
    }

    private void changeFragment() {
        getFragmentManager().beginTransaction().replace(R.id.frame, new MypageMainFragment()).addToBackStack(null).commit();
    }
}
