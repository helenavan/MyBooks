package com.helenacorp.android.mybibliotheque.model


data class User (
    var uID:String? = null,
    var pseudoFirebase:String? = null,
    var email:String? = null,
    var pathPhoto:String? = null,
    var bookList:List<BookModel>? = emptyList(),
)