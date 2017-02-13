package com.hotdog.hotapp.fragment.home;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hotdog.hotapp.R;
import com.hotdog.hotapp.activity.HomeActivity;
import com.hotdog.hotapp.network.SafeAsyncTask;
import com.hotdog.hotapp.other.CircleTransform;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.service.UploadService;
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.PetVo;

import java.io.File;

public class MypagePetFragment extends Fragment {

    private PetVo petVo, petVoNew;

    private ImageView petImage;
    private static final String urlimg = "http://150.95.141.66/hotdog/hotdog/image/pet/";
    private final int REQ_CODE_SELECT_IMAGE = 1001;

    private TextView petNameEr;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    private UploadService uploadService;
    private EditText petName, co_Date, pet_info;
    private Button search_picture, buttonPet;
    private String mImgPath, mImgTitle;
    private Bitmap bm;
    private UserService userService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage_pet, container, false);

        petVo = Util.getPetVo("petData", getActivity());
        System.out.println(petVo);
        petImage = (ImageView) rootView.findViewById(R.id.petImage);

        petName = (EditText) rootView.findViewById(R.id.petName);
        petNameEr = (TextView) rootView.findViewById(R.id.petNameEr);
        radioSexGroup = (RadioGroup) rootView.findViewById(R.id.radioSex);
        co_Date = (EditText) rootView.findViewById(R.id.co_date);
        pet_info = (EditText) rootView.findViewById(R.id.pet_info);

        search_picture = (Button) rootView.findViewById(R.id.search_picture);
        buttonPet = (Button) rootView.findViewById(R.id.buttonPet);

        petName.setText(petVo.getName());
        pet_info.setText(petVo.getInfo());
        co_Date.setText(petVo.getCo_date());

        loadPet_picture();

        search_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Util.checkStoragePermission(getActivity())) {
                            getGallery();
                        }
                    }
                });
            }
        });
        buttonPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                petVoNew = new PetVo();
                petVoNew.setUsers_no(petVo.getUsers_no());
                petVoNew.setName(petName.getText().toString());
                petVoNew.setInfo(pet_info.getText().toString());
                petVoNew.setCo_date(co_Date.getText().toString());


                new PetModifytAsyncTask().execute();
            }
        });

        return rootView;
    }

    // 프로필 로딩
    private void loadPet_picture() {
        String urlProfileImg = urlimg + "/" + petVo.getPet_image();

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(getActivity().getApplication()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(petImage);

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

                    File saveFile = new File(mImgPath);
                    new UploadTask(saveFile, petVo).execute();
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
        PetVo petVo;

        public UploadTask(File file, PetVo petVo) {
            this.file = file;
            this.petVo = petVo;
        }

        @Override
        public String call() throws Exception {

            uploadService = new UploadService();
            return uploadService.uploadPetImage(file, petVo);
        }

        @Override
        protected void onSuccess(String url_image) throws Exception {

            String urlProfileImg = urlimg + "/" + url_image;

            // Loading profile image
            Glide.with(getActivity()).load(urlProfileImg)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(getActivity().getApplication()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(petImage);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            System.out.println("-------------------- 사진 서버 에러 ------------------- " + e);
            super.onException(e);
        }
    }

    // 회원 수정 클릭
    private class PetModifytAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            userService = new UserService();

            return userService.updatePet(petVoNew);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(String flag) throws Exception {

            if ("success".equals(flag)) {
                Intent intent = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
                intent.putExtra("userNo", petVo.getUsers_no());
                intent.putExtra("callback", "mypage");
                startActivity(intent);
                getActivity().finish();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "에러, 나중에 다시 해죠", Toast.LENGTH_LONG).show();
            }
        }
    }
}
