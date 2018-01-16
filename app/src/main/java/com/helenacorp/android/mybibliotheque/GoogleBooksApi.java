package com.helenacorp.android.mybibliotheque;

import java.util.Map;

import feign.QueryMap;
import feign.RequestLine;


/**
 * Created by helena on 15/01/2018.
 */

public interface GoogleBooksApi {
    @RequestLine("GET /books/v1/volumes")
    Results findBookByISBN(@QueryMap Map<String, Object> queryParameters);
}
