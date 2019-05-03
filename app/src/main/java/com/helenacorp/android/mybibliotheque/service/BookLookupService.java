package com.helenacorp.android.mybibliotheque.service;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.helenacorp.android.mybibliotheque.GoogleBooksApi;
import com.helenacorp.android.mybibliotheque.Result;
import com.helenacorp.android.mybibliotheque.Results;
import com.helenacorp.android.mybibliotheque.model.Book;
import com.helenacorp.android.mybibliotheque.model.BookModel;

import java.io.IOException;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import feign.Feign;
import feign.Logger;
import feign.Util;
import feign.codec.Decoder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static feign.Util.ensureClosed;

/**
 * Created by helena on 15/01/2018.
 */

public class BookLookupService {
    public interface Callbacks {
        void onReponse(@Nullable Book bookModel);
        void onFailure();
    }

    public static void fetchBookByISBN(Callbacks callback, String isbn) {
        // final GoogleBooksApi googleBooksApi = connect();
        //  final Map<String, Object> queryParameters = new HashMap<>();
        //  queryParameters.put("q", "isbn:" + isbn);
        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<>(callback);

        Call<Book> call = GoogleBooksApi.retrofit.create(GoogleBooksApi.class).findBookByISBN(isbn);

        call.enqueue(new Callback<Book>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NonNull Call<Book> call, @NonNull Response<Book> response) {
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onReponse(response.body());

             //   Log.e("BookLooupService ", " => " + response + " book : " + model.getItems().get(0).getVolumeInfo().getTitle().toString());
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
            }
        });

        /*final Results apiResponse = googleBooksApi.findBookByISBN(queryParameters);
        if (apiResponse == null || apiResponse.getTotalItems() < 1) {
            throw new BookLookupException("No books found for ISBN " + isbn);
        }
        final List<Result> results = apiResponse.getItems();
        if (results == null || results.size() < 1) {
            throw new BookLookupException("Invalid items list for ISBN " + isbn);
        }
        final BookModel book = results.get(0).getBook();
        return book;*/
    }

    /*private static GoogleBooksApi connect() {
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
    }*/
}
