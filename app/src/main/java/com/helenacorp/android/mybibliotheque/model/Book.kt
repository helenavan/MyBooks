package com.helenacorp.android.mybibliotheque.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Book {

    @SerializedName("kind")
    @Expose
    var kind: String? = null

    @SerializedName("totalItems")
    @Expose
    var totalItems: Int? = null

    @SerializedName("items")
    @Expose
    var items: List<Item>? = null

    constructor() {}
    constructor(kind: String?, totalItems: Int?, items: List<Item>?) : super() {
        this.kind = kind
        this.totalItems = totalItems
        this.items = items
    }

}