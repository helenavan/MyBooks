package com.helenacorp.android.mybibliotheque;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class BookDetailActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        supportPostponeEnterTransition();

        Bundle extras = getIntent().getExtras();
        BookModel bookModel = extras.getParcelable(ViewListBooksActivity.EXTRA_BOOK_ITEM);

        ImageView imageView = findViewById(R.id.detail_couv);
        TextView textView = findViewById(R.id.detail_title);

        String imageURL = bookModel.getImageUrl();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            String imageTransitionName = extras.getString(ViewListBooksActivity.EXTRA_BOOK_IMAGE_TRANSITION_NAME);
            imageView.setTransitionName(imageTransitionName);
        }
        Picasso.with(this)
                .load(bookModel.getImageUrl())
                .noFade()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        supportPostponeEnterTransition();
                    }
                });

    }
    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
