package com.helenacorp.android.mybibliotheque;

import com.helenacorp.android.mybibliotheque.model.BookModel;

/**
 * Created by helena on 15/01/2018.
 */

public class Result {
    // the JSON field is named volumeInfo
    BookModel volumeInfo;

    public BookModel getBook() {
        return volumeInfo;
    }
}
