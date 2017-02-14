package com.hotdog.hotapp.vo;

/**
 * Created by bit123 on 2017-02-13.
 */

public class PiVo {
    private int users_no;
    private String token;
    private String device_num;
    private int temperature;
    private int humidity;

    public int getUsers_no() {
        return users_no;
    }

    public void setUsers_no(int users_no) {
        this.users_no = users_no;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDevice_num() {
        return device_num;
    }

    public void setDevice_num(String device_num) {
        this.device_num = device_num;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "PiVo{" +
                "users_no=" + users_no +
                ", token='" + token + '\'' +
                ", device_num='" + device_num + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                '}';
    }
}
