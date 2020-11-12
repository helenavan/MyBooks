package com.helenacorp.android.mybibliotheque.model

import androidx.annotation.NonNull
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by helena on 09/08/2017.
 */
@IgnoreExtraProperties
class BookModel {

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("categories")
    @Expose
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

    @SerializedName("publisher")
    @Expose
    var publisher: String? = null

    @SerializedName("authors")
    @Expose
    var authors: String? = null

    @SerializedName("idUSER")
    @Expose
    var idBookUser: String? = null



    constructor() {}

    constructor(id:String?, title: String?,author:String?, category: String?, isbn: String?,
               rating: Float, imageUrl: String?,
                islend:Boolean,isread:Boolean,editeur:String?, description: String, idBookUser:String? ) {
        this.id = id
        this.title = title
        this.authors = author
        this.category = category
        this.isbn = isbn
        this.rating = rating
        this.imageUrl = imageUrl
        this.islend = islend
        this.isread = isread
        this.publisher = editeur
        this.description= description
        this.idBookUser = idBookUser
    }

    constructor(info: String?) {
        this.publisher = info
    }

}