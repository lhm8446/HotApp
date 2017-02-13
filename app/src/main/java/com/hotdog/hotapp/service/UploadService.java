package com.hotdog.hotapp.service;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hotdog.hotapp.network.JSONResult;
import com.hotdog.hotapp.vo.PetVo;
import com.hotdog.hotapp.vo.UserVo;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;


/**
 * Created by bit123 on 2016-12-23.
 */

public class UploadService {
    private final String SERVER_URL = "http://150.95.141.66:80/hotdog";

    public String uploadUserImage(File file, UserVo userVo) throws IOException {
        String url = SERVER_URL + "/user/app/account/userprofilemodify2";

        HttpRequest request = HttpRequest.post(url);
        request.part("users_no", userVo.getUsers_no());
        request.part("userimage", file.getName(), file);

        int responseCode = request.code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            Log.d("2", "uploadUserImage: no");
        } else {
            Log.d("2", "uploadUserImage: yes");
        }

        JSONResultImage jSONResultImage = fromJSON(request, JSONResultImage.class);
        return jSONResultImage.getData();
    }

    public String uploadPetImage(File file, PetVo petVo) throws IOException {
        String url = SERVER_URL + "/user/app/account/petprofilemodify2";

        HttpRequest request = HttpRequest.post(url);
        request.part("users_no", petVo.getUsers_no());
        request.part("petimage", file.getName(), file);

        int responseCode = request.code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            Log.d("2", "uploadPetImage: no" + request.body());
        } else {
            Log.d("2", "uploadPetImage: yes");
        }

        JSONResultImage jSONResultImage = fromJSON(request, JSONResultImage.class);
        return jSONResultImage.getData();
    }

    private class JSONResultImage extends JSONResult<String> {
    }

    protected <V> V fromJSON(HttpRequest request, Class<V> target) {
        V v = null;

        try {
            Gson gson = new GsonBuilder().create();

            Reader reader = request.bufferedReader();
            v = gson.fromJson(reader, target);
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return v;
    }
}
