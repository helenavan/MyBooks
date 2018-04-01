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
    private String idUser;
    private String status;
    private String username;
    private String email;
    private String picChatUser;

    public ChatUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ChatUser(String idUser, String status, String username, String email, String picChatUser) {
        this.idUser = idUser;
        this.status = status;
        this.username = username;
        this.email = email;
        this.picChatUser = picChatUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
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
