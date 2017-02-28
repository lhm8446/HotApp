package com.hotdog.hotapp.service;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hotdog.hotapp.other.network.JSONResult;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;


public class StreamingService {
    private final String SERVER_URL = "http://150.95.141.66:80/test/cgi-bin";

    public int mobileController(String msg, String token) {
        String url = SERVER_URL + "/noti.py";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, String> data = new HashMap();
        data.put("msg", msg);
        data.put("token", token);

        if (httpRequest.form(data).created()) {
            System.out.println(httpRequest.body());
        }
        return httpRequest.code();
    }

    public String piController(String msg, String ip) {
        String url = SERVER_URL + "/send.py";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, String> data = new HashMap();
        data.put("msg", msg);
        data.put("ip", ip);

        if (httpRequest.form(data).created()) {
            System.out.println(httpRequest.body());
        }

        JSONResultpicontroller jsonResultpicontroller = fromJSON(httpRequest, JSONResultpicontroller.class);
        System.out.println("==================" + httpRequest);

        return jsonResultpicontroller.getData();
    }

    public Integer recStart(String nickname, int users_no) {
        String url = "http://150.95.141.66:8086/livestreamrecord?app=live/" + nickname + "&streamname=stream&action=startRecording&outputPath=/upload/" + users_no;
        HttpRequest httpRequest = HttpRequest.get(url).contentType("application/x-www-form-urlencoded");
        System.out.println("____________________________" + httpRequest);
        return httpRequest.code();
    }

    public Integer recStop(String nickname) {
        String url = "http://150.95.141.66:8086/livestreamrecord?app=live/" + nickname + "&streamname=stream&action=stopRecording";
        HttpRequest httpRequest = HttpRequest.get(url).contentType("application/x-www-form-urlencoded");
        System.out.println("____________________________" + httpRequest);
        return httpRequest.code();
    }

    public String imageUpload(File file, int users_no) throws IOException {
        String url = "http://150.95.141.66/hotdog/user/app/imageupload";
        HttpRequest httpRequest = HttpRequest.post(url);
        httpRequest.part("file", file.getName(), file);
        httpRequest.part("users_no", users_no);
        int responseCode = httpRequest.code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            Log.d("2", "uploadUserImage: no" + httpRequest.body());
        } else {
            Log.d("2", "uploadUserImage: yes");
        }
        JSONResultImageUpload jSONResultImageUpload = fromJSON(httpRequest, JSONResultImageUpload.class);
        return jSONResultImageUpload.getData();
    }

    public String audioUpload(File file, int users_no) throws IOException {
        String url = "http://150.95.141.66/hotdog/user/app/audioupload";
        HttpRequest httpRequest = HttpRequest.post(url);
        httpRequest.part("file", file.getName(), file);
        httpRequest.part("users_no", users_no);
        int responseCode = httpRequest.code();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            Log.d("2", "uploadUserImage: no" + httpRequest.body());
        } else {
            Log.d("2", "uploadUserImage: yes");
        }
        JSONResultAudioUpload jsonResultAudioUpload = fromJSON(httpRequest, JSONResultAudioUpload.class);
        return jsonResultAudioUpload.getData();
    }

    private class JSONResultpicontroller extends JSONResult<String> {
    }

    private class JSONResultAudioUpload extends JSONResult<String> {
    }

    private class JSONResultImageUpload extends JSONResult<String> {
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
