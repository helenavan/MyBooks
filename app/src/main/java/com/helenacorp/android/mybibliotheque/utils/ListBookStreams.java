package com.helenacorp.android.mybibliotheque.utils;

import com.helenacorp.android.mybibliotheque.GoogleBooksApi;
import com.helenacorp.android.mybibliotheque.model.Book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public class ListBookStreams {

    //1.Create a stream that will get book infos
    public static Observable<List<Book>> streamFetchBook(String isbn){
        GoogleBooksApi googleBooksApi = GoogleBooksApi.retrofit.create(GoogleBooksApi.class);
        final Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("q", "isbn:" + isbn);
        return (Observable<List<Book>>) googleBooksApi.findBookByISBN(queryParameters);
    }
}
