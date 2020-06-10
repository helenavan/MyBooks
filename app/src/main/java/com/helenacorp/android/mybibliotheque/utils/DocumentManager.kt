package com.helenacorp.android.mybibliotheque.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val TAG = "DocumentManager"

fun getIdDocumentFromFirestore(isbn:String): String {
    val mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser
    val mDB = Firebase.firestore
    var idDoc: String? = null
    mDB.collection("users").document(user!!.uid).collection("book")
            .whereEqualTo("isbn",isbn)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    idDoc = document.id
                }
            }
    Log.e(TAG,"id doc : $idDoc")
    return idDoc!!
}
