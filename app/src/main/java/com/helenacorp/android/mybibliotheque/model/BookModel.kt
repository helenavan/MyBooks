package com.helenacorp.android.mybibliotheque.model


data class BookModel(
    var id: String? = null,
    var title: String? = null,
    var description: String? = null,
    var category: String? = null,
    var isbn: String? = null,
    var rating: Double = 0.0,
    var imageUrl: String? = null,
    var islend: Boolean = false,
    var isread: Boolean = false,
    var publisher: String? = null,
    var authors: String? = null,
    var idBookUser: String? = null
)