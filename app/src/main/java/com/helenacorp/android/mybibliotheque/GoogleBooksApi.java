package com.helenacorp.android.mybibliotheque;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.helenacorp.android.mybibliotheque.model.Book;
import com.helenacorp.android.mybibliotheque.model.BookModel;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


/**
 * Created by helena on 15/01/2018.
 */

public interface GoogleBooksApi {

    /*    @RequestLine("GET /books/v1/volumes")
        Results findBookByISBN(@QueryMap Map<String, Object> queryParameters);*/
    @GET("volumes")
    //Call<Book> findBookByISBN (@Query("q") String isbn);
    Call<Book> findBookByISBN (@QueryMap Map<String, Object> queryParameters);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}
