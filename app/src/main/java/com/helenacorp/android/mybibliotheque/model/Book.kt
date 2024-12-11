package com.helenacorp.android.mybibliotheque.model


data class Book(
    var kind: String? = null,
    var totalItems: Int? = null,
    var items: List<Item>? = null
)