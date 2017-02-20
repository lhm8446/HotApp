package com.hotdog.hotapp.service;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hotdog.hotapp.other.network.JSONResult;
import com.hotdog.hotapp.vo.PetVo;
import com.hotdog.hotapp.vo.UserVo;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bit on 2017-01-24.
 */

public class UserService {
    //private final String SERVER_URL = "http://150.95.141.66:80/hotdog";
    private final String SERVER_URL = "http://192.168.1.29:8088/hotdog";

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

        JSONResultNicknameCheck jSONResultNicknameCheck = fromJSON(httpRequest, JSONResultNicknameCheck.class);

        return jSONResultNicknameCheck.getData();
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

        JSONResultUserInsert jSONResultUserInsert = fromJSON(httpRequest, JSONResultUserInsert.class);
        return jSONResultUserInsert.getData();
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

        JSONResultUserGet jSONResultUserGet = fromJSON(httpRequest, JSONResultUserGet.class);

        return jSONResultUserGet.getData();
    } // 유저정보 불러오기


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

        JSONResultUserUpdate jSONResultUserUpdate = fromJSON(httpRequest, JSONResultUserUpdate.class);
        return jSONResultUserUpdate.getData();
    }

    //2차 비밀번호 등록 유무 확인
    public String chkSecPass(UserVo userVo) {
        String url = SERVER_URL + "/user/app/secretcontrol";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, String> data = new HashMap<String, String>();
        data.put("users_no", String.valueOf(userVo.getUsers_no()));

        if (httpRequest.form(data).created()) {
            System.out.println("----- UserVo email checked ----");
        }

        JSONResultSecPassChk jSONResultSecPassChk = fromJSON(httpRequest, JSONResultSecPassChk.class);
        return jSONResultSecPassChk.getData();
    }

    //2차 비밀번호 등록
    public String registerSecPass(UserVo userVo) {
        String url = SERVER_URL + "/user/app/account/secretmodify";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, Object> data = new HashMap<String, Object>();
        System.out.println(userVo);
        data.put("nickname", userVo.getNickname());
        data.put("sec_pass_word", userVo.getSec_pass_word());
        if (httpRequest.form(data).created()) {
        }

        JSONResultSecPassReg jSONResultSecPassReg = fromJSON(httpRequest, JSONResultSecPassReg.class);
        return jSONResultSecPassReg.getData();
    }

    //2차 비밀번호 로그인
    public String SecPassLogin(UserVo userVo) {
        String url = SERVER_URL + "/user/app/secretlogin";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("users_no", userVo.getUsers_no());
        data.put("sec_pass_word", userVo.getSec_pass_word());
        if (httpRequest.form(data).created()) {
        }

        JSONResultSecPassLogin jSONResultSecPassLogin = fromJSON(httpRequest, JSONResultSecPassLogin.class);
        return jSONResultSecPassLogin.getData();
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
        }

        JSONResultUpdatePet jSONResultUpdatePet = fromJSON(httpRequest, JSONResultUpdatePet.class);
        //System.out.println(httpRequest.body() + " " + jSONResultPetUpdate.getData());
        return jSONResultUpdatePet.getData();
    }

    // 비밀번호 찾기
    public String passModify(UserVo userVo) {
        String url = SERVER_URL + "/user/app/passmodify";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("email", userVo.getEmail());
        data.put("pass_word", userVo.getPass_word());

        if (httpRequest.form(data).created()) {
        }
        JSONResultPassModify jSONResultPassModify = fromJSON(httpRequest, JSONResultPassModify.class);
        return jSONResultPassModify.getData();
    }

    // 이메일 전송
    public String sendEmail(String email) {
        String url = SERVER_URL + "/user/app/email";
        HttpRequest httpRequest = HttpRequest.post(url);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("email", email);

        if (httpRequest.form(data).created()) {
        }
        JSONResultSendEmail jSONResultSendEamil = fromJSON(httpRequest, JSONResultSendEmail.class);
        return jSONResultSendEamil.getData();
    }

    private class JSONResultPassModify extends JSONResult<String> {
    }

    private class JSONResultSendEmail extends JSONResult<String> {
    }

    private class JSONResultUserCheck extends JSONResult<UserVo> {
    }

    private class JSONResultUserGet extends JSONResult<UserVo> {
    }

    private class JSONResultEmailCheck extends JSONResult<String> {
    }

    private class JSONResultUserUpdate extends JSONResult<String> {
    }

    private class JSONResultNicknameCheck extends JSONResult<String> {
    }

    private class JSONResultUserInsert extends JSONResult<UserVo> {
    }

    private class JSONResultPetCheck extends JSONResult<PetVo> {
    }

    private class JSONResultUpdatePet extends JSONResult<String> {
    }

    private class JSONResultSecPassChk extends JSONResult<String> {
    }

    private class JSONResultSecPassReg extends JSONResult<String> {
    }

    private class JSONResultSecPassLogin extends JSONResult<String> {
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
