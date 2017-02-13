package com.hotdog.hotapp.other;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.vo.PetVo;
import com.hotdog.hotapp.vo.UserVo;

/**
 * Created by bit123 on 2017-02-09.
 */

public class Util {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 32;
    private static final int MY_PERMISSIONS_RECORD_AUDIO = 33;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 34;

    public static boolean checkAudioPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.RECORD_AUDIO)) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, Util.MY_PERMISSIONS_RECORD_AUDIO);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, Util.MY_PERMISSIONS_RECORD_AUDIO);
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    public static boolean checkCameraPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, Util.MY_PERMISSIONS_REQUEST_CAMERA);

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, Util.MY_PERMISSIONS_REQUEST_CAMERA);
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    public static boolean checkStoragePermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Util.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Util.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }


    public static void changeLoginFragment(FragmentManager fm, Object fragment) {
        fm.beginTransaction().replace(R.id.login, (android.support.v4.app.Fragment) fragment).addToBackStack(null).commit();
    }

    public static void changeJoinFragment(FragmentManager fm, Object fragment) {
        fm.beginTransaction().replace(R.id.join, (android.support.v4.app.Fragment) fragment).addToBackStack(null).commit();
    }

    public static void changeHomeFragment(FragmentManager fm, Object fragment) {
        fm.beginTransaction().replace(R.id.frame, (android.support.v4.app.Fragment) fragment).addToBackStack(null).commit();
    }

    public static void setUserVo(String key, Context context, UserVo userVo) {
        SharedPreferences data = context.getSharedPreferences(key, 0);
        SharedPreferences.Editor editor = data.edit();
        if (userVo.getUsers_no() != 0) {
            editor.putInt("users_no", userVo.getUsers_no());
        }
        if (userVo.getNickname() != null) {
            editor.putString("nickname", userVo.getNickname());
        }
        if (userVo.getEmail() != null) {
            editor.putString("email", userVo.getEmail());
        }
        if (userVo.getUsers_image() != null) {
            editor.putString("users_image", userVo.getUsers_image());
        }
        if (userVo.getPass_word() != null) {
            editor.putString("pass_word", userVo.getPass_word());
        }
        editor.commit();
    }

    public static UserVo getUserVo(String key, Context context) {
        SharedPreferences data = context.getSharedPreferences(key, 0);

        UserVo userVo = new UserVo();
        userVo.setUsers_no(data.getInt("users_no", -1));
        userVo.setNickname(data.getString("nickname", ""));
        userVo.setEmail(data.getString("email", ""));
        userVo.setUsers_image(data.getString("users_image", ""));
        userVo.setPass_word(data.getString("pass_word", ""));
        return userVo;
    }

    public static void setPetVo(String key, Context context, PetVo petVo) {
        SharedPreferences data = context.getSharedPreferences(key, 0);
        SharedPreferences.Editor editor = data.edit();
        if (petVo.getPet_no() != 0) {
            editor.putInt("pet_no", petVo.getPet_no());
        }
        if (petVo.getName() != null) {
            editor.putString("name", petVo.getName());
        }
        if (petVo.getInfo() != null) {
            editor.putString("info", petVo.getInfo());
        }
        if (petVo.getGender() != null) {
            editor.putString("gender", petVo.getGender());
        }
        if (petVo.getCo_date() != null) {
            editor.putString("co_date", petVo.getCo_date());
        }
        if (petVo.getPet_image() != null) {
            editor.putString("pet_image", petVo.getPet_image());
        }
        if (petVo.getUsers_no() != 0) {
            editor.putInt("users_no", petVo.getUsers_no());
        }
        editor.commit();
    }

    public static PetVo getPetVo(String key, Context context) {
        SharedPreferences data = context.getSharedPreferences(key, 0);

        PetVo petVo = new PetVo();
        petVo.setPet_no(data.getInt("pet_no", 0));
        petVo.setName(data.getString("name", ""));
        petVo.setInfo(data.getString("info", ""));
        petVo.setGender(data.getString("gender", ""));
        petVo.setCo_date(data.getString("co_date", ""));
        petVo.setPet_image(data.getString("pet_image", ""));
        petVo.setUsers_no(data.getInt("users_no", 0));
        return petVo;
    }
}
