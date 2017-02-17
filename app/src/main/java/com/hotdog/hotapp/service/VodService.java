package com.hotdog.hotapp.service;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hotdog.hotapp.other.network.JSONResult;
import com.hotdog.hotapp.vo.UserVo;
import com.hotdog.hotapp.vo.VideoVo;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bit123 on 2017-02-17.
 */

public class VodService {
    private final String SERVER_URL = "http://150.95.141.66:80/hotdog";

    public ArrayList<VideoVo> fetchVodUrl(UserVo userVo) throws IOException {
        String url = SERVER_URL + "/blog/app/vod";
        HttpRequest httpRequest = HttpRequest.post(url);

        System.out.println(userVo);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("users_no", userVo.getUsers_no());

        if (httpRequest.form(data).created()) {
            System.out.println(httpRequest.body());
        }

        JSONResultVodUrl jSONResultVodUrl = fromJSON(httpRequest, JSONResultVodUrl.class);
        return jSONResultVodUrl.getData();
    }

    private class JSONResultVodUrl extends JSONResult<ArrayList<VideoVo>> {
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