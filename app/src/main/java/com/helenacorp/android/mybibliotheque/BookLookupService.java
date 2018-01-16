package com.helenacorp.android.mybibliotheque;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.helenacorp.android.mybibliotheque.model.BookModel;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import feign.Feign;
import feign.Logger;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;

import static feign.Util.ensureClosed;

/**
 * Created by helena on 15/01/2018.
 */

public class BookLookupService {
    public BookModel fetchBookByISBN(String isbn) throws BookLookupException {
        final GoogleBooksApi googleBooksApi = connect();
        final Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("q", "isbn:" + isbn);
        final Results apiResponse = googleBooksApi.findBookByISBN(queryParameters);
        if (apiResponse == null || apiResponse.getTotalItems() < 1) {
            throw new BookLookupException("No books found for ISBN " + isbn);
        }
        final List<Result> results = apiResponse.getItems();
        if (results == null || results.size() < 1) {
            throw new BookLookupException("Invalid items list for ISBN " + isbn);
        }
        final BookModel book = results.get(0).getBook();
        return book;
    }

    private static GoogleBooksApi connect() {
        return Feign.builder()
                .decoder(new Decoder() {
                    @Override
                    public Object decode(Response response, Type type) throws IOException {
                        Gson gson = new Gson();
                        if (response.status() == 404) return Util.emptyValueOf(type);
                        if (response.body() == null) return null;
                        Reader reader = response.body().asReader();
                        try {
                            return gson.fromJson(reader, type);
                        } catch (JsonIOException e) {
                            if (e.getCause() != null && e.getCause() instanceof IOException) {
                                throw IOException.class.cast(e.getCause());
                            }
                            throw e;
                        } finally {
                            ensureClosed(reader);
                        }
                    }
                })
                .logger(new Logger.ErrorLogger())
                .logLevel(Logger.Level.BASIC)
                .target(GoogleBooksApi.class, "https://www.googleapis.com");
    }

    private class BookLookupException extends Exception {
        public BookLookupException(String s) {

        }
    }
}
