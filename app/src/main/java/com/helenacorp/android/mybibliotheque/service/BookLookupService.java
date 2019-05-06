package com.helenacorp.android.mybibliotheque.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.helenacorp.android.mybibliotheque.GoogleBooksApi;
import com.helenacorp.android.mybibliotheque.model.Book;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookLookupService {
    public interface Callbacks {
        void onReponse(@Nullable Book bookModel);
        void onFailure();
    }

    public static void fetchBookByISBN(Callbacks callback, String isbn, Context mContext){
        final Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("q", "isbn:" + isbn);
        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<>(callback);

        final Call<Book> call = GoogleBooksApi.retrofit.create(GoogleBooksApi.class).findBookByISBN(queryParameters);
        //ProgressDialog
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(mContext);;
        progressDialog.setMessage("Its loading....");
        progressDialog.show();
        call.enqueue(new Callback<Book>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NonNull Call<Book> call, @NonNull Response<Book> response) {
                progressDialog.dismiss();
                if(response.body() != null && response.body().getTotalItems() > 0){
                    if (callbacksWeakReference.get() != null)
                        callbacksWeakReference.get().onReponse(response.body());

                }
                Log.e("BookLooupService ", " => " + response.body().getTotalItems().toString());
            }

            @Override
            public void onFailure(@NonNull Call<Book> call, @NonNull Throwable t) {
                if (callbacksWeakReference.get() != null) callbacksWeakReference.get().onFailure();
                progressDialog.dismiss();
            }

        });

    }
}
