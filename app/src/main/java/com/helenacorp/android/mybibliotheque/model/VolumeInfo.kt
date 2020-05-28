package com.helenacorp.android.mybibliotheque.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.helenacorp.android.mybibliotheque.model.IndustryIdentifier

class VolumeInfo {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("authors")
    @Expose
    var authors: List<String>? = null

    @SerializedName("publisher")
    @Expose
    var publisher: String? = null

    @SerializedName("publishedDate")
    @Expose
    var publishedDate: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("pageCount")
    @Expose
    var pageCount: Int? = null

    @SerializedName("printType")
    @Expose
    var printType: String? = null

    @SerializedName("categories")
    @Expose
    var categories: List<String>? = null

    @SerializedName("maturityRating")
    @Expose
    var maturityRating: String? = null

    @SerializedName("allowAnonLogging")
    @Expose
    var allowAnonLogging: Boolean? = null

    @SerializedName("contentVersion")
    @Expose
    var contentVersion: String? = null

    @SerializedName("imageLinks")
    @Expose
    var imageLinks: ImageLinks? = null

    @SerializedName("language")
    @Expose
    var language: String? = null

    @SerializedName("previewLink")
    @Expose
    var previewLink: String? = null

    @SerializedName("infoLink")
    @Expose
    var infoLink: String? = null

    @SerializedName("canonicalVolumeLink")
    @Expose
    var canonicalVolumeLink: String? = null

    @SerializedName("industryIdentifiers")
    @Expose
    var industryIdentifiers: List<IndustryIdentifier>? = null

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param pageCount
     * @param infoLink
     * @param printType
     * @param allowAnonLogging
     * @param publisher
     * @param authors
     * @param canonicalVolumeLink
     * @param title
     * @param previewLink
     * @param description
     * @param imageLinks
     * @param contentVersion
     * @param categories
     * @param language
     * @param publishedDate
     * @param maturityRating
     */
    constructor(title: String?, authors: List<String>?, publisher: String?, publishedDate: String?, description: String?, pageCount: Int?, printType: String?, categories: List<String>?, maturityRating: String?, allowAnonLogging: Boolean?, contentVersion: String?,
                imageLinks: ImageLinks?, language: String?, previewLink: String?,
                infoLink: String?, canonicalVolumeLink: String?, industryIdentifiers: List<IndustryIdentifier>?) : super() {
        this.title = title
        this.authors = authors
        this.publisher = publisher
        this.publishedDate = publishedDate
        this.description = description
        this.pageCount = pageCount
        this.printType = printType
        this.categories = categories
        this.maturityRating = maturityRating
        this.allowAnonLogging = allowAnonLogging
        this.contentVersion = contentVersion
        this.imageLinks = imageLinks
        this.language = language
        this.previewLink = previewLink
        this.infoLink = infoLink
        this.canonicalVolumeLink = canonicalVolumeLink
        this.industryIdentifiers = industryIdentifiers
    }

}