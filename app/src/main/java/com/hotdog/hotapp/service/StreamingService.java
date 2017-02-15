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

/**
 * Created by tydrk on 2017-01-26.
 */

public class StreamingService {
    private final String SERVER_URL = "http://150.95.141.66:80/test/cgi-bin";

    public String piController(String msg, String ip) {
        String url = SERVER_URL + "/send.py";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, String> data = new HashMap<String, String>();
        data.put("msg", msg);
        data.put("ip", ip + "");

        if (httpRequest.form(data).created()) {
            System.out.println(httpRequest.body());
        }

        JSONResultpicontroller jsonResultpicontroller = fromJSON(httpRequest, JSONResultpicontroller.class);
        System.out.println("==================" + httpRequest);

        return jsonResultpicontroller.getData();
    }

    public Integer recStart(String nickname, int users_no) {
        String url = "http://hotdog:hotdog@150.95.141.66:8086/livestreamrecord?app=live/" + nickname + "&streamname=stream&action=startRecording&outputPath=/upload/" + users_no;
        String url1 = "http://admin:admin@150.95.141.66:8086/livestreamrecord?app=live/nap12&streamname=stream&action=startRecording&outputPath=/upload/5";
        int response = HttpRequest.get(url1).code();

        return response;
    }

    public Integer recStop(String nickname) {
        String url = "http://hotdog:hotdog@150.95.141.66:8086/livestreamrecord?app=live/" + nickname + "&streamname=stream&action=stopRecording";
        String url1 = "http://admin:admin@150.95.141.66:8086/livestreamrecord?app=live/nap12&streamname=stream&action=stopRecording";

        int response = HttpRequest.get(url1).code();

        return response;
    }

    public String audioUpload(File file) throws IOException {
        String url = "http://150.95.141.66/hotdog/user/app/audioupload";
        HttpRequest httpRequest = HttpRequest.post(url);
        httpRequest.part("file", file.getName(), file);

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
