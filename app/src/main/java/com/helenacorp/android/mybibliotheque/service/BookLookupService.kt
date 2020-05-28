package com.helenacorp.android.mybibliotheque.service

import com.helenacorp.android.mybibliotheque.service.BookLookupService
import com.helenacorp.android.mybibliotheque.GoogleBooksApi
import android.app.ProgressDialog
import android.content.Context
import androidx.annotation.RequiresApi
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.helenacorp.android.mybibliotheque.model.Book
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import java.util.HashMap

object BookLookupService {
    fun fetchBookByISBN(callback: Callbacks?, isbn: String, mContext: Context?) {
        val queryParameters: MutableMap<String?, Any?> = HashMap()
        queryParameters["q"] = "isbn:$isbn"
        val callbacksWeakReference = WeakReference(callback)
        val call = GoogleBooksApi.retrofit.create(GoogleBooksApi::class.java).findBookByISBN(queryParameters)
        //ProgressDialog
        val progressDialog: ProgressDialog
        progressDialog = ProgressDialog(mContext)
        progressDialog.setMessage("Its loading....")
        progressDialog.show()
        call!!.enqueue(object : Callback<Book?> {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            override fun onResponse(call: Call<Book?>, response: Response<Book?>) {
                progressDialog.dismiss()
                if (response.body() != null && response.body()!!.totalItems!! > 0) {
                    if (callbacksWeakReference.get() != null) callbacksWeakReference.get()!!.onReponse(response.body())
                } else {
                    Toast.makeText(mContext, "Le livre n'existe pas dans la base ", Toast.LENGTH_LONG).show()
                }
                Log.e("BookLooupService ", " => " + response.body()!!.totalItems.toString())
            }

            override fun onFailure(call: Call<Book?>, t: Throwable) {
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get()!!.onFailure()
                progressDialog.dismiss()
            }
        })
    }

    interface Callbacks {
        fun onReponse(bookModel: Book?)
        fun onFailure()
    }
}