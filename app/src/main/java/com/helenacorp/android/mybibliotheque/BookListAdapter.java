package com.helenacorp.android.mybibliotheque;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;


/**
 * Created by helena on 09/08/2017.
 */

public class BookListAdapter extends FirebaseRecyclerAdapter<BookListAdapter.ViewHolder, BookModel> {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private final BookItemListener bookItemListener;

    public BookListAdapter(Query query, @Nullable ArrayList<BookModel> bookModelArrayList, @Nullable ArrayList<String> keys, BookItemListener bookItemListener) {
        super(query, bookModelArrayList, keys);
        this.bookItemListener = bookItemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookListAdapter.ViewHolder holder, final int position) {
        final BookModel model = getItem(position);
        holder.txtTitle.setText(model.getTitle());
        holder.txtAutorFirstname.setText(model.getNameAutor());
        holder.txtAutorLastname.setText(model.getLastnameAutor());
        holder.isbnNumber.setText(model.getIsbn());
        holder.ratingBar.setRating(model.getRating());
        holder.pic.getContext();

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(R.color.bleu_gray)
                .borderWidthDp(3)
                .cornerRadiusDp(30)
                .oval(false)
                .build();

        Picasso.with(holder.context)
                .load(model.getImageUrl())
                .fit()
                .transform(transformation)
                .into(holder.pic);
        ViewCompat.setTransitionName(holder.pic, model.getNameAutor());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();
                final String idBooks = holder.txtTitle.getText().toString();
                Log.e("tag", idBooks);
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                final Query booksQuery = databaseReference.child("books").orderByChild("title").equalTo(idBooks);

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.context);
                builder.setMessage("Voulez-vous supprimer ?").setCancelable(false)
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //retrieve the path of title from detail item and delete it
                                booksQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                                            bookSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("tag", "oncancelled", databaseError.toException());
                                    }
                                });
                                Log.d("TAG", "delete?");
                                notifyItemRemoved(position);
                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setTitle("Confirmer");
                dialog.show();
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookItemListener.onAnimalItemClick(holder.getAdapterPosition(),model, holder.pic);
            }
        });

    }

    @Override
    protected void itemAdded(BookModel item, String key, int position) {
        Log.d("MyAdapter", "Added a new item to the adapter.");
        notifyDataSetChanged();
    }

    @Override
    protected void itemChanged(BookModel item, BookModel newItem, String key, int position) {
        Log.d("MyAdapter", "Changed an item.");
        notifyDataSetChanged();
    }

    @Override
    protected void itemRemoved(BookModel item, String key, int position) {
        Log.d("MyAdapter", "Removed an item from the adapter.");
    }

    @Override
    protected void itemMoved(BookModel item, String key, int oldPosition, int newPosition) {
        Log.d("MyAdapter", "Moved an item.");
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

        }
    }
}



