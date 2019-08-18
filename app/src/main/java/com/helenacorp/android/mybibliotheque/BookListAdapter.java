package com.helenacorp.android.mybibliotheque;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.helenacorp.android.mybibliotheque.model.BookModel;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Scene;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionManager;


/**
 * Created by helena on 09/08/2017.
 */

public class BookListAdapter extends FirebaseRecyclerBookAdapter<BookListAdapter.ViewHolder, BookModel> {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TransitionManager mTransitionManager;
    private Scene mScene1;
    private Scene mScene2;
    private OnBookItemClick onBookItemClick;
    private ArrayList<BookModel> mItems = new ArrayList<>();
    private ArrayList<BookModel> mItemFilter = new ArrayList<>();
    private RequestManager glide;
    private Uri img_uri;

    public BookListAdapter(Query query, @Nullable ArrayList<BookModel> bookModelArrayList, @Nullable ArrayList<String> keys,
                           OnBookItemClick bookItemClick,RequestManager glide) {
        super(query, bookModelArrayList, keys, bookItemClick, glide);
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
        holder.txtAutorLastname.setText(model.getLastnameAutor());
        holder.category.setText(model.getCategory());
        holder.resume.setText(model.getInfo());
        holder.isbnNumber.setText(model.getIsbn());
        holder.ratingBar.setRating(model.getRating());
        holder.keyBook.setText(model.getBookid());
        holder.pic.getContext();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference desertRef = storageReference.child("couvertures/" + user.getUid() + holder.txtTitle.getText().toString().trim() + ".jpg");

        desertRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                img_uri = uri;
                Glide.with(holder.context).load(uri).apply(RequestOptions.circleCropTransform()).into(holder.pic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        // Create a reference to the file to delete
        final String idBooks = holder.isbnNumber.getText().toString();
        Log.e("tag", idBooks);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        databaseReference.keepSynced(true);
        final Query booksQuery = databaseReference.child("books").orderByChild("isbn").equalTo(idBooks);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.context);
                builder.setMessage("Voulez-vous supprimer ?").setCancelable(false)
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete the file
                                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                    }
                                });
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
                notifyDataSetChanged();
                return true;
            }

        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (position != RecyclerView.NO_POSITION) {
                    ViewGroup container = (ViewGroup) view.findViewById(R.id.container);
/*                    TransitionInflater transitionInflater = TransitionInflater.from(holder.context);
                    mTransitionManager = transitionInflater.inflateTransitionManager(
                            R.transition.transition_manager, container);
                    mScene1 = Scene.getSceneForLayout(container,
                            R.layout.activity_view_list_books, holder.context);
                    mScene2 = Scene.getSceneForLayout(container,
                            R.layout.activity_book_detail, holder.context);*/
                    //  goToScene2(view);
                    //send data to detailbookactivity

                    String title = model.getTitle();
                    String name = model.getLastnameAutor();
                    String categorie = model.getCategory();
                    String resume = model.getInfo();
                    String isbn = model.getIsbn();
                    Float rating = model.getRating();
                    String bookKey = model.getBookid();

                    Intent intent = new Intent(holder.context, BookDetailActivity.class);
                    intent.putExtra("bookid", bookKey);
                    intent.putExtra("title", title);
                    intent.putExtra("lastnameAutor", name);
                    intent.putExtra("category", categorie);
                    intent.putExtra("info", resume);
                    intent.putExtra("isbn", isbn);
                   // intent.putExtra("imageUrl", img_uri.toString());
                    intent.putExtra("rating", rating);

                    holder.context.startActivity(intent);
                }
            }
        });

    }
    public void updateList(ArrayList<BookModel> list){
        mItems = list;
        notifyDataSetChanged();
    }
    //to use setTransitionManager
    public void goToScene1(View view) {
        mTransitionManager.transitionTo(mScene1);
    }

    public void goToScene2(View view) {
        mTransitionManager.transitionTo(mScene2);
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
        notifyDataSetChanged();
    }

    @Override
    protected void itemMoved(BookModel item, String key, int oldPosition, int newPosition) {
        Log.d("MyAdapter", "Moved an item.");
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtAutorLastname, isbnNumber, resume, keyBook, category;
        private RatingBar ratingBar;
        private ImageView pic;
        private Context context;


        public ViewHolder(View v) {
            super(v);
            txtTitle = (TextView) v.findViewById(R.id.title_item);

            txtAutorLastname = (TextView) v.findViewById(R.id.autorLastName_item);

            isbnNumber = (TextView) v.findViewById(R.id.isbn_item);

            ratingBar = (RatingBar) v.findViewById(R.id.ratingbar);

            pic = (ImageView) v.findViewById(R.id.pic_item);

            category = (TextView) v.findViewById(R.id.category_list);

            resume = v.findViewById(R.id.resum_item);

            context = pic.getContext();

            keyBook =(TextView) v.findViewById(R.id.id_item);
        }
    }
}



