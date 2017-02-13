package com.hotdog.hotapp.vo;

/**
 * Created by bit on 2017-01-24.
 */

public class UserVo {
    private int users_no;
    private String email;
    private String nickname;
    private String pass_word;
    private int sec_pass_word;
    private String infomation;
    private String users_image;
    private int follower_num;
    private int follwing_num;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFollower_num() {
        return follower_num;
    }

    public void setFollower_num(int follower_num) {
        this.follower_num = follower_num;
    }

    public int getFollwing_num() {
        return follwing_num;
    }

    public void setFollwing_num(int follwing_num) {
        this.follwing_num = follwing_num;
    }

    public String getInfomation() {
        return infomation;
    }

    public void setInfomation(String infomation) {
        this.infomation = infomation;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPass_word() {
        return pass_word;
    }

    public void setPass_word(String pass_word) {
        this.pass_word = pass_word;
    }

    public int getSec_pass_word() {
        return sec_pass_word;
    }

    public void setSec_pass_word(int sec_pass_word) {
        this.sec_pass_word = sec_pass_word;
    }

    public String getUsers_image() {
        return users_image;
    }

    public void setUsers_image(String users_image) {
        this.users_image = users_image;
    }

    public int getUsers_no() {
        return users_no;
    }

    public void setUsers_no(int users_no) {
        this.users_no = users_no;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "email='" + email + '\'' +
                ", users_no=" + users_no +
                ", nickname='" + nickname + '\'' +
                ", pass_word='" + pass_word + '\'' +
                ", sec_pass_word=" + sec_pass_word +
                ", infomation='" + infomation + '\'' +
                ", users_image='" + users_image + '\'' +
                ", follower_num=" + follower_num +
                ", follwing_num=" + follwing_num +
                '}';
    }
}
