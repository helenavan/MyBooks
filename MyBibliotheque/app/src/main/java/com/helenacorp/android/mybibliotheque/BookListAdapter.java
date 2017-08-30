package com.helenacorp.android.mybibliotheque;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by helena on 09/08/2017.
 */

public class BookListAdapter extends FirebaseListAdapter<BookModel> {
    CustomFilter customFilter;
    private TextView txtTitle, txtAutorFirstname, txtAutorLastname;
    private RatingBar ratingBar;
    private ImageView pic;
    private List<BookModel> arraylist = new ArrayList<BookModel>();
    private List<BookModel> booknameArrayList = new ArrayList<BookModel>();

    /**
     * @param mRef        The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                    combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param mLayout     This is the mLayout used to represent a single list item. You will be responsible for populating an
     *                    instance of the corresponding view with the data from an instance of mModelClass.
     * @param activity    The activity containing the ListView
     */
    public BookListAdapter(Query mRef, int mLayout, Activity activity) {
        super(mRef, BookModel.class, mLayout, activity);
    }

    @Override
    protected void populateView(final View v, BookModel model) {
        txtTitle = (TextView) v.findViewById(R.id.title_item);
        txtTitle.setText(model.getTitle());
        txtAutorFirstname = (TextView) v.findViewById(R.id.autorName_item);
        txtAutorFirstname.setText(model.getNameAutor());
        txtAutorLastname = (TextView) v.findViewById(R.id.autorLastName_item);
        txtAutorLastname.setText(model.getLastnameAutor());
        ratingBar = (RatingBar) v.findViewById(R.id.ratingbar);
        ratingBar.setRating(model.getRating());
        pic = (ImageView) v.findViewById(R.id.pic_item);

        // progressDialog.dismiss();
        Picasso.with(v.getContext())
                .load(model.getImageUrl())
                .into(pic);

    }
   /* @Override
     public void filter(String s){
        if(customFilter==null)
        {
            customFilter=new CustomFilter(this,arraylist);
        }
        //return customFilter;
    }*/
  /*  @Override
    public void filter(String charText) {
        booknameArrayList.clear();
        if (charText.isEmpty()) {
            booknameArrayList.addAll(arraylist);
        } else {
            charText = charText.toLowerCase();
            for (BookModel wp : arraylist) {
                if (wp.getNameAutor().toLowerCase().contains(charText)) {
                   booknameArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }*/
}



