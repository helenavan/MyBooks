package com.helenacorp.android.mybibliotheque.model

import android.net.Uri

class User {

    var uID:String? = null
    var pseudoFirebase:String? = null
    var email:String? = null
    var pathPhoto:String? = null
    var bookList:List<BookModel>? = null

    constructor() {}

    constructor(pathPhoto: String?){
        this.pathPhoto = pathPhoto
    }
    constructor(uID: String?, pseudoFirebase: String?, email: String?, pathPhoto: String?, bookList: List<BookModel>?) {
        this.uID = uID
        this.pseudoFirebase = pseudoFirebase
        this.email = email
        this.pathPhoto = pathPhoto
        this.bookList = bookList
    }

}