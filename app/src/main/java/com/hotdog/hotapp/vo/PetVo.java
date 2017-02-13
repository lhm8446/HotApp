package com.hotdog.hotapp.vo;

/**
 * Created by bit on 2017-01-24.
 */

public class PetVo {
    private int pet_no;
    private String name;
    private String co_date;
    private String gender;
    private String age;
    private String info;
    private String pet_image;
    private int sec_pass_word;
    private int users_no;

    public int getPet_no() {
        return pet_no;
    }

    public void setPet_no(int pet_no) {
        this.pet_no = pet_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCo_date() {
        return co_date;
    }

    public void setCo_date(String co_date) {
        this.co_date = co_date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPet_image() {
        return pet_image;
    }

    public void setPet_image(String pet_image) {
        this.pet_image = pet_image;
    }

    public int getSec_pass_word() {
        return sec_pass_word;
    }

    public void setSec_pass_word(int sec_pass_word) {
        this.sec_pass_word = sec_pass_word;
    }

    public int getUsers_no() {
        return users_no;
    }

    public void setUsers_no(int users_no) {
        this.users_no = users_no;
    }

    @Override
    public String toString() {
        return "PetVo{" +
                "pet_no=" + pet_no +
                ", name='" + name + '\'' +
                ", co_date='" + co_date + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", info='" + info + '\'' +
                ", pet_image='" + pet_image + '\'' +
                ", sec_pass_word=" + sec_pass_word +
                ", users_no=" + users_no +
                '}';
    }
}
