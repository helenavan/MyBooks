package com.helenacorp.android.mybibliotheque;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;


/**
 * Created by helena on 09/08/2017.
 */

public class BookListAdapter extends FirebaseRecyclerAdapter<BookListAdapter.ViewHolder, BookModel> {
    public BookListAdapter(Query query, int mLayout,
                           Activity activity) {
        super(query, BookModel.class, mLayout, activity);
    }

    @Override
    public BookListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookListAdapter.ViewHolder holder, int position) {
        BookModel model = getItem(position);
        holder.txtTitle.setText(model.getTitle());
        holder.txtAutorFirstname.setText(model.getNameAutor());
        holder.txtAutorLastname.setText(model.getLastnameAutor());
        holder.isbnNumber.setText(model.getIsbn());
        holder.ratingBar.setRating(model.getRating());
        Context context = holder.pic.getContext();

        Picasso.with(context)
                .load(model.getImageUrl())
                .into(holder.pic);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtAutorFirstname, txtAutorLastname, isbnNumber;
        private RatingBar ratingBar;
        private ImageView pic;

        public ViewHolder(View v) {
            super(v);
            txtTitle = (TextView) v.findViewById(R.id.title_item);

            txtAutorFirstname = (TextView) v.findViewById(R.id.autorName_item);

            txtAutorLastname = (TextView) v.findViewById(R.id.autorLastName_item);

            isbnNumber = (TextView) v.findViewById(R.id.isbn_item);

            ratingBar = (RatingBar) v.findViewById(R.id.ratingbar);

            pic = (ImageView) v.findViewById(R.id.pic_item);

            // progressDialog.dismiss();

        }
    }


}



