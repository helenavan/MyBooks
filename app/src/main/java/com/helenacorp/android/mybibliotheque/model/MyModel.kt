package com.helenacorp.android.mybibliotheque.model

import androidx.annotation.NonNull
import com.google.firebase.firestore.Exclude

open class MyModel {
    @Exclude
    var id: String? = null

    fun <T : MyModel?> withId(@NonNull id: String?): T {
        this.id = id
        return this as T
    }
}