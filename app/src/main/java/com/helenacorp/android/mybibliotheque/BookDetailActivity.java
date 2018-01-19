package com.helenacorp.android.mybibliotheque;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.helenacorp.android.mybibliotheque.model.BookModel;
import com.squareup.picasso.Picasso;

public class BookDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    public static final String EXTRA_CAR_ITEM = "com.helenacorp.android.mybibliotheque";
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private AppBarLayout appbar;
    private CollapsingToolbarLayout collapsing;
   // private ImageView couv;
    private TextView title, name, isbn, resume, title_two;
    private RatingBar ratingBar;
    ConstraintLayout constraintLayout;
    private FrameLayout framelayoutTitle;
    private LinearLayout linearlayoutTitle;
    private Toolbar toolbar;
    private SimpleDraweeView couv;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_book_detail);

        supportPostponeEnterTransition();
        BookModel bookItem = getIntent().getParcelableExtra(EXTRA_CAR_ITEM);
        couv = findViewById(R.id.pic_item);
        isbn = findViewById(R.id.isbn_item);
        title = findViewById(R.id.title_item);
        title_two = findViewById(R.id.title_item_two);
        resume = findViewById(R.id.resum_item);
        name = findViewById(R.id.autorLastName_item);
        ratingBar = findViewById(R.id.ratingbar);
        constraintLayout = findViewById(R.id.container);
        linearlayoutTitle = (LinearLayout)findViewById( R.id.linearlayout_title );
        collapsing = (CollapsingToolbarLayout)findViewById( R.id.collapsing );
        toolbar = (Toolbar)findViewById( R.id.toolbar );
        appbar = (AppBarLayout)findViewById( R.id.app_bar );
        Bundle bundle = getIntent().getExtras();
        //ok
        isbn.setText(bundle.getString("isbn"));
        title.setText(bundle.getString("title"));
        title_two.setText(bundle.getString("title"));
        resume.setText(bundle.getString("info"));
        resume.setMovementMethod(new ScrollingMovementMethod());
        name.setText(bundle.getString("name"));
        ratingBar.setRating(bundle.getFloat("rating"));
        String url = bundle.getString("couv");
        Picasso.with(getApplicationContext()).load(url).into(couv);

        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(this);
        setSupportActionBar(toolbar);
        startAlphaAnimation(title_two, 0, View.INVISIBLE);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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

            if(!mIsTheTitleVisible) {
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
            if(mIsTheTitleContainerVisible) {
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

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation;
        if (visibility == View.VISIBLE) alphaAnimation = new AlphaAnimation(0f, 1f);
        else alphaAnimation = new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

}
