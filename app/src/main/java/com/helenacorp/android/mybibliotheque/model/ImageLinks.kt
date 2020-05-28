package com.helenacorp.android.mybibliotheque.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

/**
 * Created by helena on 16/01/2018.
 */
class ImageLinks {
    @SerializedName("smallThumbnail")
    @Expose
    var smallThumbnail: String? = null

    @SerializedName("thumbnail")
    @Expose
    var thumbnail: String? = null

    @SerializedName("small")
    @Expose
    var small: String? = null

    @SerializedName("medium")
    @Expose
    var medium: String? = null

    @SerializedName("large")
    @Expose
    var large: String? = null

    @SerializedName("extraLarge")
    @Expose
    var extraLarge: String? = null

}