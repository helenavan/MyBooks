package com.helenacorp.android.mybibliotheque;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.Query;

/**
 * Created by helena on 09/08/2017.
 */

public class BookListAdapter extends FirebaseListAdapter<BookModel> {
    private TextView txtTitle, txtAutorFirstname, txtAutorLastname;
    private RatingBar ratingBar;
    private ImageView pic;

    /**
     * @param mRef        The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                    combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param mModelClass Firebase will marshall the data at a location into an instance of a class that you provide
     * @param mLayout     This is the mLayout used to represent a single list item. You will be responsible for populating an
     *                    instance of the corresponding view with the data from an instance of mModelClass.
     * @param activity    The activity containing the ListView
     */
    public BookListAdapter(Query mRef, int mLayout, Activity activity) {
        super(mRef, BookModel.class, mLayout, activity);
    }

    @Override
    protected void populateView(View v, BookModel model) {
        txtTitle = (TextView) v.findViewById(R.id.title_item);
        txtTitle.setText(model.getTitle());
        txtAutorFirstname = (TextView) v.findViewById(R.id.autorName_item);
        txtAutorFirstname.setText(model.getNameAutor());
        txtAutorLastname = (TextView) v.findViewById(R.id.autorLastName_item);
        txtAutorLastname.setText(model.getLastnameAutor());
        ratingBar = (RatingBar) v.findViewById(R.id.ratingbar);
        ratingBar.setRating(model.getRating());
        pic = (ImageView) v.findViewById(R.id.pic_item);

        Glide.with(v.getContext())
                .load(model.getImageUrl())
                .into(pic);
    }

}
