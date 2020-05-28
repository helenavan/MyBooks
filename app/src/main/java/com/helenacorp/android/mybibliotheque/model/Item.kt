package com.helenacorp.android.mybibliotheque.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class Item {
    @SerializedName("kind")
    @Expose
    var kind: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("etag")
    @Expose
    var etag: String? = null

    @SerializedName("selfLink")
    @Expose
    var selfLink: String? = null

    @SerializedName("volumeInfo")
    @Expose
    var volumeInfo: VolumeInfo? = null

    @SerializedName("searchInfo")
    @Expose
    var searchInfo: SearchInfo? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param id
     * @param searchInfo
     * @param etag
     * @param volumeInfo
     * @param selfLink
     * @param kind
     */
    constructor(kind: String?, id: String?, etag: String?, selfLink: String?, volumeInfo: VolumeInfo?, searchInfo: SearchInfo?) : super() {
        this.kind = kind
        this.id = id
        this.etag = etag
        this.selfLink = selfLink
        this.volumeInfo = volumeInfo
        this.searchInfo = searchInfo
    }

}