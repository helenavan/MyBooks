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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        holder.pic.getContext();

        //Picasso p = new Picasso.Builder(context) .memoryCache(new LruCache(24000)) .build();
        Picasso.with(holder.context)
                .load(model.getImageUrl())
                .resize(100, 100)
                .into(holder.pic);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtAutorFirstname, txtAutorLastname, isbnNumber;
        private RatingBar ratingBar;
        private ImageView pic;
        private Context context;

        public ViewHolder(View v) {
            super(v);
            txtTitle = (TextView) v.findViewById(R.id.title_item);

            txtAutorFirstname = (TextView) v.findViewById(R.id.autorName_item);

            txtAutorLastname = (TextView) v.findViewById(R.id.autorLastName_item);

            isbnNumber = (TextView) v.findViewById(R.id.isbn_item);

            ratingBar = (RatingBar) v.findViewById(R.id.ratingbar);

            pic = (ImageView) v.findViewById(R.id.pic_item);

            context = pic.getContext();

            // progressDialog.dismiss();

        }
    }

}



