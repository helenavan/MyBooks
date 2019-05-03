package com.helenacorp.android.mybibliotheque.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Book {
    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("totalItems")
    @Expose
    private Integer totalItems;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Book() {
    }

    /**
     *
     * @param items
     * @param totalItems
     * @param kind
     */
    public Book(String kind, Integer totalItems, List<Item> items) {
        super();
        this.kind = kind;
        this.totalItems = totalItems;
        this.items = items;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

}
