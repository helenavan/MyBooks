package com.helenacorp.android.mybibliotheque.model;

/**
 * Created by helena on 15/03/2018.
 */

public class MessageModel {

    private String id;
    private String message;
    private String userName;
    private String userPic;
    private String imageUserUrl;

    public MessageModel(){
    }

    public MessageModel(String id, String message, String userName, String userPic, String imageUserUrl) {
        this.id = id;
        this.message = message;
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
