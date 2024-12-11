package com.helenacorp.android.mybibliotheque.model


data class Item (
    var kind: String? = null,
    var id: String? = null,
    var etag: String? = null,
    var selfLink: String? = null,
    var volumeInfo: VolumeInfo? = null,
    var searchInfo: SearchInfo? = null
)