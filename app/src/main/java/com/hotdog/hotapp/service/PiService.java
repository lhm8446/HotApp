package com.hotdog.hotapp.service;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hotdog.hotapp.other.network.JSONResult;
import com.hotdog.hotapp.vo.PiVo;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bit123 on 2017-02-13.
 */

public class PiService {
    private final String SERVER_URL = "http://150.95.141.66:80/hotdog/pi";

    // pi 정보 입력
    public String piInsert(PiVo piVo) {
        String url = SERVER_URL + "/app/raspberry/insert";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("users_no", piVo.getUsers_no());
        data.put("device_num", piVo.getDevice_num());
        data.put("ip_address", piVo.getIp_address());


        if (httpRequest.form(data).created()) {
            System.out.println(httpRequest.body());
        }

        JSONResultPiInsert jSONResultPiInsert = fromJSON(httpRequest, JSONResultPiInsert.class);

        return jSONResultPiInsert.getData();
    }

    // pi 정보 입력
    public PiVo getinfo(int users_no) {
        String url = SERVER_URL + "/app/raspberry/getinfo";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("users_no", users_no);

        if (httpRequest.form(data).created()) {
        }

        JSONResultGetinfo jSONResultGetinfo = fromJSON(httpRequest, JSONResultGetinfo.class);
        System.out.println("==================" + jSONResultGetinfo.getData());

        return jSONResultGetinfo.getData();
    }


    private class JSONResultPiInsert extends JSONResult<String> {
    }

    private class JSONResultGetinfo extends JSONResult<PiVo> {
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
