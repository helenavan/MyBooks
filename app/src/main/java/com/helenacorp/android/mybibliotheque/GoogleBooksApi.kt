package com.helenacorp.android.mybibliotheque

import com.helenacorp.android.mybibliotheque.model.Book
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * Created by helena on 15/01/2018.
 */
interface GoogleBooksApi {
    /*    @RequestLine("GET /books/v1/volumes")
        Results findBookByISBN(@QueryMap Map<String, Object> queryParameters);*/
    @GET("volumes")
    fun  //Call<Book> findBookByISBN (@Query("q") String isbn);
            findBookByISBN(@QueryMap queryParameters: MutableMap<String?, Any?>?): Call<Book?>?

    companion object {
        @JvmField
        val retrofit:Retrofit = Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}