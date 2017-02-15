package com.hotdog.hotapp.fragment.home;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import com.hotdog.hotapp.other.CircleTransform;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.UploadService;
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.PetVo;

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;

public class MypagePetFragment extends Fragment {
    private View view;

    private PetVo petVo, petVoNew;

    private ImageView petImage;
    private static final String urlimg = "http://150.95.141.66/hotdog/hotdog/image/user/";
    private final int REQ_CODE_SELECT_IMAGE = 1001;

    private TextView petNameEr, petAge, co_Date;
    private RadioGroup radioSexGroup;
    private RadioButton radioMaleButton, radioFemaleButton;
    private UploadService uploadService;
    private EditText petName, pet_info;
    private Button search_picture, buttonPet;

    private String mImgPath, mImgTitle;
    private Bitmap bm;
    private UserService userService;
    private File saveFile;
    private Calendar cal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage_pet, container, false);
        Util.checkStoragePermission(getActivity());
        petVo = Util.getPetVo("petData", getActivity());

        petImage = (ImageView) rootView.findViewById(R.id.petImage);
        petName = (EditText) rootView.findViewById(R.id.petName);
        petNameEr = (TextView) rootView.findViewById(R.id.petNameEr);
        radioSexGroup = (RadioGroup) rootView.findViewById(R.id.radioSex);
        co_Date = (TextView) rootView.findViewById(R.id.co_date);
        pet_info = (EditText) rootView.findViewById(R.id.pet_info);
        radioMaleButton = (RadioButton) rootView.findViewById(R.id.radioMale);
        radioFemaleButton = (RadioButton) rootView.findViewById(R.id.radioFemale);
        search_picture = (Button) rootView.findViewById(R.id.search_picture);
        buttonPet = (Button) rootView.findViewById(R.id.buttonPet);
        petAge = (TextView) rootView.findViewById(R.id.petAge);

        petName.setText(petVo.getName());
        pet_info.setText(petVo.getInfo());
        petAge.setText(petVo.getAge());
        co_Date.setText(petVo.getCo_date().toString());

        if ("male".equals(petVo.getGender())) {
            radioMaleButton.setChecked(true);
            radioFemaleButton.setChecked(false);
        } else if ("female".equals(petVo.getGender())) {
            radioMaleButton.setChecked(false);
            radioFemaleButton.setChecked(true);
        }


        loadPet_picture();
        cal = Calendar.getInstance(TimeZone.getDefault());

        init();

        return rootView;
    }

    private void init() {
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
        //펫 생일
        petAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePicker = null;
                if ("".equals(petAge.getText().toString())) {
                    datePicker = new DatePickerDialog(getActivity(),
                            datePickerListener,
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH));
                } else {
                    String[] strArr = petAge.getText().toString().split("/");
                    datePicker = new DatePickerDialog(getActivity(),
                            datePickerListener,
                            Integer.parseInt(strArr[2]),
                            Integer.parseInt(strArr[1]) - 1,
                            Integer.parseInt(strArr[0]));
                }
                datePicker.setCancelable(false);
                datePicker.setTitle("Select the date");
                datePicker.show();
            }
        });
        //함께 한 날
        co_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePicker = null;
                if ("".equals(co_Date.getText().toString())) {
                    datePicker = new DatePickerDialog(getActivity(),
                            datePickerListener1,
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH));
                } else {
                    String[] strArr = co_Date.getText().toString().split("/");
                    datePicker = new DatePickerDialog(getActivity(),
                            datePickerListener1,
                            Integer.parseInt(strArr[2]),
                            Integer.parseInt(strArr[1]) - 1,
                            Integer.parseInt(strArr[0]));
                }
                datePicker.setCancelable(false);
                datePicker.setTitle("Select the date");
                datePicker.show();
            }
        });

        buttonPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (petName.getText().toString().length() <= 0) {
                    petNameEr.setVisibility(view.VISIBLE);
                    return;
                } else {
                    petNameEr.setVisibility(view.GONE);
                }

                petVoNew = new PetVo();
                if (radioSexGroup.getCheckedRadioButtonId() == R.id.radioMale) {
                    petVoNew.setGender("male");
                } else if (radioSexGroup.getCheckedRadioButtonId() == R.id.radioFemale) {
                    petVoNew.setGender("female");
                }
                petVoNew.setUsers_no(petVo.getUsers_no());
                petVoNew.setName(petName.getText().toString());

                petVoNew.setInfo(pet_info.getText().toString());
                petVoNew.setCo_date(co_Date.getText().toString());
                petVoNew.setAge(petAge.getText().toString());
                if (saveFile != null) {
                    new UploadTask(saveFile, petVoNew).execute();
                }
                new PetModifytAsyncTask().execute();
            }
        });

    }


    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(selectedMonth + 1);
            String day1 = String.valueOf(selectedDay);
            if (month1.length() == 1) {
                month1 = "0" + month1;
            }
            if (day1.length() == 1) {
                day1 = "0" + day1;
            }
            petAge.setText(day1 + "/" + month1 + "/" + year1);

        }
    };
    private DatePickerDialog.OnDateSetListener datePickerListener1 = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(selectedMonth + 1);
            String day1 = String.valueOf(selectedDay);
            if (month1.length() == 1) {
                month1 = "0" + month1;
            }
            if (day1.length() == 1) {
                day1 = "0" + day1;
            }
            co_Date.setText(day1 + "/" + month1 + "/" + year1);

        }
    };

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
                    saveFile = new File(mImgPath);

                    petImage.setImageBitmap(bm);

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

            Toast.makeText(getActivity().getApplicationContext(), "사진이 수정됬습니다.", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            System.out.println("-------------------- 사진 서버 에러 ------------------- " + e);
            super.onException(e);
        }
    }

    // 펫 수정 클릭
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
