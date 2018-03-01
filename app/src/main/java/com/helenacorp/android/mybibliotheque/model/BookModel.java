package com.helenacorp.android.mybibliotheque.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by helena on 09/08/2017.
 */

public class BookModel {
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    private String category;
    private String isbn;
    private String lastnameAutor;
    private String userName;
    @SerializedName("userid")
    @Expose
    private String userid;
    private float rating;
    private String imageUrl;
    @SerializedName("info")
    @Expose
    private String info;
    @SerializedName("authors")
    @Expose
    private List<String> authors;
    @SerializedName("imageLinks")
    @Expose
    private ImageLinks imageLinks;

    public ImageLinks getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(ImageLinks imageLinks) {
        this.imageLinks = imageLinks;
    }

    //bug du constructeur vide
    public BookModel() {

    }

    public BookModel(String title, String category, String isbn, String lastnameAutor,
                     String userName, String userid, float rating, String imageUrl, String info) {
        this.title = title;
        this.category = category;
        this.isbn = isbn;
        this.lastnameAutor = lastnameAutor;
        this.userName = userName;
        this.userid = userid;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.info = info;
    }

    public BookModel(String info){
        this.info = info;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getLastnameAutor() {
        return lastnameAutor;
    }

    public void setLastnameAutor(String lastnameAutor) {
        this.lastnameAutor = lastnameAutor;
    }

    public String getCode() {
        return userName;
    }

    public void setCode(String code) {
        this.userName = code;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getInfo() {return info;}

    public void setInfo(String info) {this.info = info;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
