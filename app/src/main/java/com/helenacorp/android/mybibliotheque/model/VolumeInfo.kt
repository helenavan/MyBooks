package com.helenacorp.android.mybibliotheque.model


data class VolumeInfo(
    var title: String? = null,
    var authors: List<String>? = null,
    var publisher: String? = null,
    var publishedDate: String? = null,
    var description: String? = null,
    var pageCount: Int? = null,
    var printType: String? = null,
    var categories: List<String>? = null,
    var maturityRating: String? = null,
    var allowAnonLogging: Boolean? = null,
    var contentVersion: String? = null,
    var imageLinks: ImageLinks? = null,
    var language: String? = null,
    var previewLink: String? = null,
    var infoLink: String? = null,
    var canonicalVolumeLink: String? = null,
    var industryIdentifiers: List<IndustryIdentifier>? = null
)