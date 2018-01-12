package com.helenacorp.android.mybibliotheque;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BookDetailActivity extends AppCompatActivity {
    public static final String EXTRA_CAR_ITEM = "com.helenacorp.android.mybibliotheque";
    private ImageView couv;
    private TextView title, name, isbn, firstname;
    private RatingBar ratingBar;
    ConstraintLayout constraintLayout;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        supportPostponeEnterTransition();
        BookModel bookItem = getIntent().getParcelableExtra(EXTRA_CAR_ITEM);
        couv = findViewById(R.id.pic_item);
        isbn = findViewById(R.id.isbn_item);
        title = findViewById(R.id.title_item);
        firstname = findViewById(R.id.autorName_item);
        name = findViewById(R.id.autorLastName_item);
        ratingBar = findViewById(R.id.ratingbar);
        constraintLayout = findViewById(R.id.container);
        Bundle bundle = getIntent().getExtras();
        //ok
        isbn.setText(bundle.getString("isbn"));
        title.setText(bundle.getString("title"));
        firstname.setText(bundle.getString("prenom"));
        name.setText(bundle.getString("name"));
        ratingBar.setRating(bundle.getFloat("rating"));
        String url = bundle.getString("couv");
        Picasso.with(getApplicationContext()).load(url).into(couv);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailActivity.this, ViewListBooksActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BookDetailActivity.this, ViewListBooksActivity.class);
        startActivity(intent);
        // moveTaskToBack(true);
    }
}
