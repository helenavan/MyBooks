package com.helenacorp.android.mybibliotheque;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private EditText titleName, firstName, lastName;
    private Button btnClean, btnAdd, btnPic;
    private RatingBar ratingBar;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_book);

        titleName = (EditText) findViewById(R.id.title_submit);
        firstName = (EditText) findViewById(R.id.nameAutor_submit);
        lastName = (EditText) findViewById(R.id.autorLastName_submit);
        btnClean = (Button) findViewById(R.id.btn_clean_submit);
        btnAdd = (Button) findViewById(R.id.btn_add_submit);
        btnPic = (Button) findViewById(R.id.btn_photoBook_submit);
        ratingBar = (RatingBar) findViewById(R.id.submit_rating);

        btnAdd.setOnClickListener(this);
        btnClean.setOnClickListener(this);
        btnPic.setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mSource.equals(SyncStateContract.Constants.SOURCE_SAVED)) {
            inflater.inflate(R.menu.menu_submit, menu);
        } else {
            inflater.inflate(R.menu.menu_submit, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_photo:
                onLaunchCamera();
            default:
                break;
        }
        return false;
    }

    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageLabel.setImageBitmap(imageBitmap);
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }

    //encodage
    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(SyncStateContract.Constants.FIREBASE_CHILD_RESTAURANTS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mRestaurant.getPushId())
                .child("imageUrl");
        ref.setValue(imageEncoded);
    }

    public void validation() {
        if (titleName.getText().length() == 0 || firstName.length() == 0) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "Ha! N'avez-vous pas oublié quelque chose?", duration);
            toast.show();
        } else {
            sendBook(btnAdd);
        }
    }

    public void sendBook(View view) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String userName = user.getDisplayName();
        //write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users").child(user.getUid()).child("books");

        BookModel bookModel = new BookModel(titleName.getText().toString(), null, null, firstName.getText().toString(),
                lastName.getText().toString(), userName, null, ratingBar.getNumStars());
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
        if (view == btnPic) {

        }
    }

}
