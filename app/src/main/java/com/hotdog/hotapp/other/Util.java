package com.hotdog.hotapp.other;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.vo.PetVo;
import com.hotdog.hotapp.vo.PiVo;
import com.hotdog.hotapp.vo.UserVo;

/**
 * Created by bit123 on 2017-02-09.
 */

public class Util {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 32;
    private static final int MY_PERMISSIONS_RECORD_AUDIO = 33;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 34;
    private static int TYPE_WIFI = 1;
    private static int TYPE_MOBILE = 2;
    private static int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }


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

    public static void changeHomeFragment(FragmentManager fm, Object fragment, String tag) {
        fm.beginTransaction().replace(R.id.frame, (android.support.v4.app.Fragment) fragment, tag).addToBackStack(null).commit();
    }

    public static void logout(Context context) {
        SharedPreferences data = context.getSharedPreferences("userData", 0);
        SharedPreferences.Editor editor = data.edit();
        editor.clear();
        editor.commit();

        data = context.getSharedPreferences("repeatShow", 0);
        editor = data.edit();
        editor.clear();
        editor.commit();

        data = context.getSharedPreferences("email", 0);
        editor = data.edit();
        editor.clear();
        editor.commit();

        data = context.getSharedPreferences("petData", 0);
        editor = data.edit();
        editor.clear();
        editor.commit();

        data = context.getSharedPreferences("piData", 0);
        editor = data.edit();
        editor.clear();
        editor.commit();

        data = context.getSharedPreferences("auto", 0);
        editor = data.edit();
        editor.clear();
        editor.commit();

        data = context.getSharedPreferences("stream", 0);
        editor = data.edit();
        editor.clear();
        editor.commit();

        data = PreferenceManager.getDefaultSharedPreferences(context);
        editor = data.edit();
        editor.clear();
        editor.commit();
    }

    public static void setUserVo(Context context, UserVo userVo) {
        SharedPreferences data = context.getSharedPreferences("userData", 0);
        SharedPreferences.Editor editor = data.edit();
        editor.clear();

        editor.putInt("users_no", userVo.getUsers_no());
        editor.putString("nickname", userVo.getNickname());
        editor.putString("email", userVo.getEmail());
        editor.putString("users_image", userVo.getUsers_image());
        editor.putString("pass_word", userVo.getPass_word());
        editor.commit();
    }

    public static UserVo getUserVo(Context context) {
        SharedPreferences data = context.getSharedPreferences("userData", 0);

        UserVo userVo = new UserVo();
        userVo.setUsers_no(data.getInt("users_no", -1));
        userVo.setNickname(data.getString("nickname", ""));
        userVo.setEmail(data.getString("email", ""));
        userVo.setUsers_image(data.getString("users_image", ""));
        userVo.setPass_word(data.getString("pass_word", ""));
        return userVo;
    }

    public static void setPetVo(Context context, PetVo petVo) {
        SharedPreferences data = context.getSharedPreferences("petData", 0);
        SharedPreferences.Editor editor = data.edit();
        editor.clear();

        editor.putInt("pet_no", petVo.getPet_no());
        editor.putString("name", petVo.getName());
        editor.putString("info", petVo.getInfo());
        editor.putString("gender", petVo.getGender());
        editor.putString("age", petVo.getAge());
        editor.putString("co_date", petVo.getCo_date());
        editor.putString("pet_image", petVo.getPet_image());
        editor.putInt("users_no", petVo.getUsers_no());
        editor.commit();
    }

    public static PetVo getPetVo(Context context) {
        SharedPreferences data = context.getSharedPreferences("petData", 0);

        PetVo petVo = new PetVo();
        petVo.setPet_no(data.getInt("pet_no", 0));
        petVo.setName(data.getString("name", ""));
        petVo.setInfo(data.getString("info", ""));
        petVo.setGender(data.getString("gender", ""));
        petVo.setAge(data.getString("age", ""));
        petVo.setCo_date(data.getString("co_date", ""));
        petVo.setPet_image(data.getString("pet_image", ""));
        petVo.setUsers_no(data.getInt("users_no", 0));
        return petVo;
    }

    public static void setPiVo(Context context, PiVo piVo) {
        SharedPreferences data = context.getSharedPreferences("piData", 0);
        SharedPreferences.Editor editor = data.edit();
        editor.clear();

        editor.putInt("users_no", piVo.getUsers_no());
        editor.putString("device_num", piVo.getToken());
        editor.putString("ip_address", piVo.getDevice_num());
        editor.putInt("temperature", piVo.getTemperature());
        editor.putString("sec_token", piVo.getSec_token());
        editor.commit();
    }

    public static PiVo getPiVo(Context context) {
        SharedPreferences data = context.getSharedPreferences("piData", 0);

        PiVo piVo = new PiVo();
        piVo.setUsers_no(data.getInt("users_no", 0));
        piVo.setToken(data.getString("device_num", ""));
        piVo.setDevice_num(data.getString("ip_address", ""));
        piVo.setTemperature(data.getInt("temperature", 0));
        piVo.setSec_token(data.getString("sec_token", ""));
        return piVo;
    }


}
