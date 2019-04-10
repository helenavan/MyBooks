package com.helenacorp.android.mybibliotheque;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.helenacorp.android.mybibliotheque.Controllers.Activities.AccountActivity;
import com.helenacorp.android.mybibliotheque.model.BookModel;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;


import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

public class BookDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    public static final String EXTRA_CAR_ITEM = "com.helenacorp.android.mybibliotheque";
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private AppBarLayout appbar;
    private CollapsingToolbarLayout collapsing;
    private ImageView couv, arrow;
    private TextView title, name, category, resume, title_two;
    private RatingBar ratingBar;
    ConstraintLayout constraintLayout;
    private FrameLayout framelayoutTitle;
    private LinearLayoutCompat linearlayoutTitle;
    private Toolbar toolbar;
    private FloatingActionButton edit_detail;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference ref;
    private String key;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_book_detail);

        arrow = findViewById(R.id.arrow_detail);
        edit_detail = findViewById(R.id.edit_detail);
        supportPostponeEnterTransition();
        final BookModel bookItem = getIntent().getParcelableExtra(EXTRA_CAR_ITEM);
        couv = findViewById(R.id.pic_item);
        category = findViewById(R.id.category_item);
        title = findViewById(R.id.title_item);
        title_two = findViewById(R.id.title_item_two);
        resume = findViewById(R.id.resum_item);
        name = findViewById(R.id.autorLastName_item);
        ratingBar = findViewById(R.id.ratingbar);
        constraintLayout = findViewById(R.id.container);
        linearlayoutTitle = (LinearLayoutCompat) findViewById(R.id.linearlayout_title);
        collapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appbar = (AppBarLayout) findViewById(R.id.app_bar);
        //retrieve extra in bundle
        final Bundle bundle = getIntent().getExtras();
        category.setText(bundle.getString("category"));
        title.setText(bundle.getString("title"));
        title_two.setText(bundle.getString("title"));
        resume.setText(bundle.getString("info"));
        resume.setMovementMethod(new ScrollingMovementMethod());
        name.setText(bundle.getString("lastnameAutor"));
        ratingBar.setRating(bundle.getFloat("rating"));
        final String url = bundle.getString("imageUrl");
        //retrieve key's child of books node
        key = bundle.getString("bookid");

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(R.color.bleu_gray)
                .borderWidthDp(3)
                .cornerRadiusDp(20)
                .oval(false)
                .build();
        Picasso.get().load(url).fit().transform(transformation).into(couv);

        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(this);
        setSupportActionBar(toolbar);
        startAlphaAnimation(title_two, 0, View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid()).child("books");

        edit_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(BookDetailActivity.this);
                View view1 = layoutInflater.inflate(R.layout.dialog_detail, null);
                final AlertDialog.Builder alertD = new AlertDialog.Builder(BookDetailActivity.this);
                alertD.setView(view1);

                final EditText resume_dialog = view1.findViewById(R.id.resum_dialog);
                resume_dialog.setText(resume.getText());
                ImageView head = findViewById(R.id.head_dialog);
                TextView headTxt = view1.findViewById(R.id.title_dialog);
                headTxt.setText(title.getText());
                alertD.setPositiveButton("Enregistrer\nles modifs", new DialogInterface.OnClickListener() {
                    //retrieve resume , change it, save in view and upgrade in firebase
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //pass de string key as child in datareference
                        DatabaseReference infoDb = ref.child(key);
                        String updateResum = resume_dialog.getText().toString();
                        resume.setText(updateResum);
                        Map<String, Object> map = new HashMap<>();
                        map.put("info", updateResum);
                        infoDb.updateChildren(map);
                        Toast.makeText(BookDetailActivity.this, "RE-enregistré", Toast.LENGTH_SHORT).show();
                    }
                });
                alertD.setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = alertD.create();
                dialog.show();
            }

        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(title_two, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(title_two, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation;
        if (visibility == View.VISIBLE) alphaAnimation = new AlphaAnimation(0f, 1f);
        else alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }


}
