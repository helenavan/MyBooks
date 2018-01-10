package com.helenacorp.android.mybibliotheque;

import android.widget.ImageView;

/**
 * Created by helena on 10/01/2018.
 */

public interface BookItemListener {
    void onAnimalItemClick(int pos, BookModel bookModel, ImageView shareImageView);
}
