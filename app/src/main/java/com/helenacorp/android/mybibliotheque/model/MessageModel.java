package com.helenacorp.android.mybibliotheque.model;

/**
 * Created by helena on 15/03/2018.
 */

public class MessageModel {

    private String id;
    private String message;
    private String userNameModel;
    private String userPic;
    private String imageUserUrl;

    public MessageModel(){
    }

    public MessageModel(String id, String message, String userNameModel, String userPic, String imageUserUrl) {
        this.id = id;
        this.message = message;
        this.userNameModel = userNameModel;
        this.userPic = userPic;
        this.imageUserUrl = imageUserUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserNameModel() {
        return userNameModel;
    }

    public void setUserName(String userName) {
        this.userNameModel = userNameModel;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getImageUserUrl() {
        return imageUserUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUserUrl = imageUrl;
    }
}
