package com.hotdog.hotapp.service;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hotdog.hotapp.network.JSONResult;
import com.hotdog.hotapp.vo.PetVo;
import com.hotdog.hotapp.vo.UserVo;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bit on 2017-01-24.
 */

public class UserService {
    private final String SERVER_URL = "http://150.95.141.66:80/hotdog";

    // (로그인, 회원가입)이메일 체크
    public String userEmailCheck(String email) {
        String url = SERVER_URL + "/user/app/emailcheck";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", email);

        if (httpRequest.form(data).created()) {
            System.out.println("----- UserVo email checked ----");
        }

        JSONResultEmailCheck jsonResultEmailCheck = fromJSON(httpRequest, JSONResultEmailCheck.class);
        System.out.println("==================" + jsonResultEmailCheck.getData());

        return jsonResultEmailCheck.getData();
    }

    // (최종 로그인)이메일 + 비밀번호 체크
    public UserVo userEmailPasswordCheck(String email, String password) {
        String url = SERVER_URL + "/user/app/login";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", email);
        data.put("pass_word", password);

        if (httpRequest.form(data).created()) {
            System.out.println("----- Users login checked ----");
        }

        JSONResultUserCheck jsonResultUserCheck = fromJSON(httpRequest, JSONResultUserCheck.class);

        return jsonResultUserCheck.getData();
    }

    // (회원가입)닉네임 체크
    public String userNickCheck(String nickname) {
        String url = SERVER_URL + "/user/nickCheck";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, String> data = new HashMap<String, String>();
        data.put("nickname", nickname);

        if (httpRequest.form(data).created()) {
            System.out.println("----- UserVo nickname checked ----");
        }

        JSONResultEmailCheck jsonResultEmailCheck = fromJSON(httpRequest, JSONResultEmailCheck.class);

        return jsonResultEmailCheck.getData();
    }

    // (회원가입)유저정보 저장
    public UserVo userInsert(UserVo userVo) {
        String url = SERVER_URL + "/user/app/join";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", userVo.getEmail());
        data.put("pass_word", userVo.getPass_word());
        data.put("nickname", userVo.getNickname());

        if (httpRequest.form(data).created()) {
            System.out.println("----- Users login checked ----");
        }

        JSONResultUserCheck jsonResultUserCheck = fromJSON(httpRequest, JSONResultUserCheck.class);
        System.out.println(jsonResultUserCheck.getData());
        return jsonResultUserCheck.getData();
    }

    // 유저정보 불러오기
    public UserVo getUser(int userNo) {
        String url = SERVER_URL + "/user/app/getuser";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("userno", userNo);
        if (httpRequest.form(data).created()) {
            System.out.println("----- Users login checked ----");
        }

        JSONResultUserCheck jsonResultUserCheck = fromJSON(httpRequest, JSONResultUserCheck.class);

        System.out.println(jsonResultUserCheck.getData());
        return jsonResultUserCheck.getData();
    }

    //유저 이미지 제외 정보 수정
    public String userModify(UserVo userVo) {
        String url = SERVER_URL + "/user/app/account/userprofilemodify";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, String> data = new HashMap<String, String>();
        data.put("users_no", String.valueOf(userVo.getUsers_no()));
        data.put("nickname", userVo.getNickname());
        data.put("pass_word", userVo.getPass_word());

        if (httpRequest.form(data).created()) {
            System.out.println("----- UserVo email checked ----");
        }

        JSONResultEmailCheck jsonResultEmailCheck = fromJSON(httpRequest, JSONResultEmailCheck.class);
        System.out.println("==================" + jsonResultEmailCheck.getData());

        return jsonResultEmailCheck.getData();
    }


    // 펫 정보 불러오기
    public PetVo getPet(int userNo) {
        String url = SERVER_URL + "/user/app/getpet";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("users_no", userNo);

        if (httpRequest.form(data).created()) {
            System.out.println("----- Pet loading ----");
        }
        JSONResultPetCheck jsonResultPetCheck = fromJSON(httpRequest, JSONResultPetCheck.class);
        System.out.println(jsonResultPetCheck.getData());
        return jsonResultPetCheck.getData();
    }

    // 펫 정보 수정하기
    public String updatePet(PetVo petVo) {
        String url = SERVER_URL + "/user/app/account/petprofilemodify";
        HttpRequest httpRequest = HttpRequest.post(url);
        System.out.println(petVo);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("users_no", toString().valueOf(petVo.getUsers_no()));
        data.put("petname", petVo.getName());
        data.put("petinfo", petVo.getInfo());
        data.put("age", petVo.getAge());
        data.put("gender", petVo.getGender());
        data.put("co_date", petVo.getCo_date());

        if (httpRequest.form(data).created()) {
            System.out.println("----- Pet update ----");
        }else{
            System.out.println(httpRequest.body());
        }

        JSONResultPetUpdate jSONResultPetUpdate = fromJSON(httpRequest, JSONResultPetUpdate.class);
        System.out.println(httpRequest.body());
        return jSONResultPetUpdate.getData();
    }

    private class JSONResultUserCheck extends JSONResult<UserVo> {
    }

    private class JSONResultEmailCheck extends JSONResult<String> {
    }

    private class JSONResultPetCheck extends JSONResult<PetVo> {
    }

    private class JSONResultPetUpdate extends JSONResult<String> {
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
