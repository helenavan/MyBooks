package com.helenacorp.android.mybibliotheque.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class IndustryIdentifier {
    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("identifier")
    @Expose
    var identifier: String? = null

}