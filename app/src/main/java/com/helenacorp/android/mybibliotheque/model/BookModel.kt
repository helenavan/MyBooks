package com.helenacorp.android.mybibliotheque.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

/**
 * Created by helena on 09/08/2017.
 */
@IgnoreExtraProperties
class BookModel {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null
    var category: String? = null

    @SerializedName("isbn")
    @Expose
    var isbn: String? = null

    var rating = 0f
    @SerializedName("imageUrl")
    @Expose
    var imageUrl: String? = null

    @SerializedName("islend")
    @Expose
    var islend : Boolean  = false

    @SerializedName("isread")
    @Expose
    var isread :Boolean = false

    @SerializedName("info")
    @Expose
    var info: String? = null

    @SerializedName("author")
    @Expose
    var author: String? = null

    @SerializedName("idUSER")
    @Expose
    var idBookUser: String? = null

    constructor() {}

    constructor(title: String?,author:String?, category: String?, isbn: String?,
               rating: Float, imageUrl: String?,
                islend:Boolean,isread:Boolean, info: String, idBookUser:String? ) {
        this.title = title
        this.author = author
        this.category = category
        this.isbn = isbn
        this.rating = rating
        this.imageUrl = imageUrl
        this.islend = islend
        this.isread = isread
        this.info = info
        this.idBookUser = idBookUser
    }

    constructor(info: String?) {
        this.info = info
    }

}