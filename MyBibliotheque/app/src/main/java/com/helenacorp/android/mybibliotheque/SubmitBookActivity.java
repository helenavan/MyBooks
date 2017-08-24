package com.helenacorp.android.mybibliotheque;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

public class SubmitBookActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final String SOURCE_SAVED = "saved";

    private EditText titleName, firstName, lastName;
    private Button btnClean, btnAdd, btnPic;
    private RatingBar ratingBar;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String imageEncoded;
    private ImageView mImageBook;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_book);

        titleName = (EditText) findViewById(R.id.title_submit);
        firstName = (EditText) findViewById(R.id.nameAutor_submit);
        lastName = (EditText) findViewById(R.id.autorLastName_submit);
        btnClean = (Button) findViewById(R.id.btn_clean_submit);
        btnAdd = (Button) findViewById(R.id.btn_add_submit);
        mImageBook = (ImageView) findViewById(R.id.submit_photoView);

        ratingBar = (RatingBar) findViewById(R.id.submit_rating);
        ratingBar.getNumStars();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), Float.toString(rating), Toast.LENGTH_SHORT).show();

            }

        });

        btnAdd.setOnClickListener(this);
        btnClean.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activitySubmit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("Yeah!");

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_photo:
                onLaunchCamera();
            default:
                break;
        }
        return true;
    }

    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(SubmitBookActivity.this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == SubmitBookActivity.this.RESULT_OK) {
            Bundle extras = data.getExtras();

            try {
                imageBitmap = (Bitmap) extras.get("data");
                mImageBook.setImageBitmap(imageBitmap);
                encodeBitmapAndSaveToFirebase(imageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //encodage
    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        BookModel model = new BookModel();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .child("books")
                .child(model.getImageUrl());

        ref.push().setValue(imageEncoded);
    }

    public void validation() {
        if (titleName.getText().length() == 0 || firstName.length() == 0) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "Ha! N'avez-vous pas oublié quelque chose?", duration);
            toast.show();
        } else {
            sendBook();
        }
    }

    public void sendBook() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String userName = user.getDisplayName();

      /*  imageBitmap = ((BitmapDrawable) mImageBook.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);*/

        //write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users").child(user.getUid()).child("books");
        BookModel bookModel = new BookModel(titleName.getText().toString(), null, null, firstName.getText().toString(),
                lastName.getText().toString(), userName, null, ratingBar.getRating(), imageEncoded);
        ref.push().setValue(bookModel);
        Toast toast = Toast.makeText(SubmitBookActivity.this, "envoyé!", Toast.LENGTH_SHORT);
        toast.show();
        finish();
    }

    public void clearEditText(EditText editText) {
        editText.setText("");
    }

    @Override
    public void onClick(View view) {
        if (view == btnAdd) {
            validation();
        }
        if (view == btnClean) {
            clearEditText(titleName);
            clearEditText(firstName);
            clearEditText(lastName);
        }
    }

}
