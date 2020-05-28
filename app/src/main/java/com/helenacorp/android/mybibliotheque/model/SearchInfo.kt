package com.helenacorp.android.mybibliotheque.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class SearchInfo {
    @SerializedName("textSnippet")
    @Expose
    var textSnippet: String? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param textSnippet
     */
    constructor(textSnippet: String?) : super() {
        this.textSnippet = textSnippet
    }

}