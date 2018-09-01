package com.example.ryan.mychatapp;

/**
 * Created by Ryan on 7/29/2018.
 */

public class Requests {

    private String user_name, user_status, user_thumb_image;

    public Requests(String user_name, String user_status, String user_thumb_image) {
        this.user_name = user_name;
        this.user_status = user_status;
        this.user_thumb_image = user_thumb_image;
    }

    public Requests()
    {

    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_thumb_image() {
        return user_thumb_image;
    }

    public void setUser_thumb_image(String user_thumb_image) {
        this.user_thumb_image = user_thumb_image;
    }
}
