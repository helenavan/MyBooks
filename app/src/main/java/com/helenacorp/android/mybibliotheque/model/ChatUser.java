package com.helenacorp.android.mybibliotheque.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by helena on 21/03/2018.
 */
/**
 * The Class ChatUse is a Java Bean class that represents a single user.
 */
@IgnoreExtraProperties
public class ChatUser implements Serializable{
    private String user_id;
    private String status;
    private String username;
    private String email;
    private String picChatUser;
    private String device_token;
    private boolean online;

    public boolean getOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }


    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public ChatUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ChatUser(String user_id, String status, String username, String email, String picChatUser, String device_token, boolean online) {
        this.user_id = user_id;
        this.status = status;
        this.username = username;
        this.email = email;
        this.picChatUser = picChatUser;
        this.device_token = device_token;
        this.online = online;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicChatUser() {
        return picChatUser;
    }

    public void setPicChatUser(String picChatUser) {
        this.picChatUser = picChatUser;
    }
}
