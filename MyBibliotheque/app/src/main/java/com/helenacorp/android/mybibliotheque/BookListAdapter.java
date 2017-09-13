package com.helenacorp.android.mybibliotheque;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by helena on 09/08/2017.
 */

public class BookListAdapter extends FirebaseListAdapter<BookModel> {
    CustomFilter customFilter;
    private TextView txtTitle, txtAutorFirstname, txtAutorLastname, isbnNumber;
    private RatingBar ratingBar;
    private ImageView pic;
    private ArrayList<BookModel> modelArrayList = new ArrayList<>();
    private ArrayList<BookModel> modelArrayListCopy = new ArrayList<>();

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
        isbnNumber = (TextView) v.findViewById(R.id.isbn_item);
        isbnNumber.setText(model.getIsbn());
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
  /* public Filter getFilter(){
       return  new Filter() {
           @Override
           protected FilterResults performFiltering(CharSequence charSequence) {
               String charString = charSequence.toString();
               if ( charString.isEmpty()){
                   modelArrayList = modelArrayListCopy;
               }else {
                   ArrayList<BookModel> filteredList = new ArrayList<>();
                   for ( BookModel bookModel : modelArrayListCopy){
                       if(bookModel.getTitle().toString().toLowerCase().contains(charString)){
                           filteredList.add(bookModel);
                       }
                   }
                   modelArrayList = filteredList;
               }
               FilterResults filterResults = new FilterResults();
               filterResults.values = modelArrayList;
               return filterResults;
           }

           @Override
           protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
               modelArrayList = (ArrayList<BookModel>) filterResults.values;
               notifyDataSetChanged();
           }
       };
   }*/
}



