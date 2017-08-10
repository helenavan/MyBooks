package com.helenacorp.android.mybibliotheque;

/**
 * Created by helena on 09/08/2017.
 */

public class BookModel {
    private String title;
    private String category;
    private String isbn;
    private String nameAutor;
    private String lastnameAutor;
    private String code;
    private String userid;

    public BookModel(String title, String category, String isbn, String nameAutor, String lastnameAutor, String code, String userid) {
        this.title = title;
        this.category = category;
        this.isbn = isbn;
        this.nameAutor = nameAutor;
        this.lastnameAutor = lastnameAutor;
        this.code = code;
        this.userid = userid;
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

    public String getNameAutor() {
        return nameAutor;
    }

    public void setNameAutor(String nameAutor) {
        this.nameAutor = nameAutor;
    }

    public String getLastnameAutor() {
        return lastnameAutor;
    }

    public void setLastnameAutor(String lastnameAutor) {
        this.lastnameAutor = lastnameAutor;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
