package com.helenacorp.android.mybibliotheque;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;
import com.helenacorp.android.mybibliotheque.model.BookModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SubmitBookActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final int ZXING_CAMERA_PERMISSION = 1;

    String result;
    private Class<?> mClss;
    private EditText titleName, lastName;
    private EditText isbn;
    private String isbnId;
    private Button btnClean, btnAdd, btnIsbn, btnVerif;
    private RatingBar ratingBar;
    private TextView resum;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ImageView mImageBook, mImageBookVisible;
    private Uri imguri;
    private DatabaseReference databaseReference;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_book);

        titleName = (EditText) findViewById(R.id.title_submit);
        lastName = (EditText) findViewById(R.id.autorLastName_submit);
        resum = findViewById(R.id.submit_resum);
        btnClean = (Button) findViewById(R.id.btn_clean_submit);
        btnAdd = (Button) findViewById(R.id.btn_add_submit);
        btnVerif = findViewById(R.id.btn_verify_isbn_submit);
        mImageBook = (ImageView) findViewById(R.id.submit_photoView);
        mImageBookVisible = (ImageView) findViewById(R.id.submit_viewpic);
        btnIsbn = (Button) findViewById(R.id.submit_btn_isbn);
        isbn = findViewById(R.id.isbn);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        ratingBar = (RatingBar) findViewById(R.id.submit_rating);
        ratingBar.getNumStars();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                Toast.makeText(getApplicationContext(), Float.toString(rating), Toast.LENGTH_SHORT).show();

            }

        });

        btnAdd.setOnClickListener(this);
        btnClean.setOnClickListener(this);
        btnVerif.setOnClickListener(this);
        btnIsbn.setOnClickListener(this);
        mImageBook.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activitySubmit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

  /*  public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(SubmitBookActivity.this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                imguri = data.getData();
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imguri);
                    // mImageBook.setVisibility(View.INVISIBLE);
                    mImageBookVisible.setMaxWidth(200);
                    mImageBookVisible.setMaxHeight(300);
                    mImageBookVisible.setImageBitmap(imageBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String returnValue = data.getStringExtra("barcode");
                isbn.setText(returnValue);
            }
        }
    }

    //validate label from autor and title
    public void validation() {
        if (titleName.getText().length() == 0 || lastName.length() == 0 || validationIsbn()) {
            if (titleName.getText().length() == 0 || lastName.length() == 0) {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, R.string.toast_2, duration);
                toast.show();
            }
        } else {
            if (mImageBookVisible != null) sendBookcover();
        }
    }

    //if isbn of book exist
    public boolean validationIsbn() {
        String isbnId = isbn.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("books");
        Query isbnQuery = ref.orderByChild("isbn").equalTo(isbnId);
        isbnQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(SubmitBookActivity.this, R.string.toast, Toast.LENGTH_SHORT).show();
                } else {
                    if (mImageBookVisible != null) sendBookcover();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return true;
    }

    //compresse la photo sélectionnée pour la couverture et l'envoie sur firebase
    public void sendBookcover() {
        final String userName = user.getDisplayName();
        mImageBookVisible.setDrawingCacheEnabled(true);
        mImageBookVisible.buildDrawingCache();
        Bitmap bitmap = mImageBookVisible.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("books").push();
        final StorageReference userPic = storageReference.child("couvertures/" + titleName.getText().toString() + lastName.getText().toString() + ".jpg");
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = userPic.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String imageDownloadUrl=taskSnapshot.getDownloadUrl().toString();
                BookModel bookModel = new BookModel(titleName.getText().toString().trim(), null, isbn.getText().toString(),
                        lastName.getText().toString(), userName, null, ratingBar.getRating(), taskSnapshot.getDownloadUrl().toString(), resum.getText().toString());
                //Save image info in to firebase database
               // databaseReference.push();
                Map<String, Object> map = new HashMap<>();
                map.put("title", bookModel.getTitle());
                map.put("isbn", bookModel.getIsbn());
                map.put("lastnameAutor", bookModel.getLastnameAutor());
                map.put("rating", bookModel.getRating());
                map.put("imageUrl", bookModel.getImageUrl());
                map.put("info",bookModel.getInfo());
                map.put("id", databaseReference.getKey());
                databaseReference.setValue(map);
                //databaseReference.setValue(bookModel);
                Toast.makeText(SubmitBookActivity.this, "Enregistré!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clearEditText(EditText editText) {
        editText.setText("");
    }

    public void cleanText(TextView textView) {
        textView.setText("");
    }

    public void cleanCouv(ImageView img) {
        if (img != null) {
            img.setImageBitmap(null);
            img.getDrawingCache(false);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnVerif) {
            new FetchBookTask().execute(getISBN());
        }
        if (view == btnAdd) {
            validation();
        }
        if (view == btnClean) {
            clearEditText(titleName);
            clearEditText(lastName);
            clearEditText(isbn);
            resum.setText("");
            cleanCouv(mImageBookVisible);
        }
        if (view == mImageBook) {
            shooseToCamera();
        }
        if (view == btnIsbn) {
            launchActivity(SimpleScannerActivity.class);
        }
    }

    public void shooseToCamera() {
       /* Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(intent, 0);*/
        Intent intent = new Intent();
        //change intent.setType("images/*") by ("*/*")
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CAPTURE);
    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private String getISBN() {
        final EditText isbnField = (EditText) findViewById(R.id.isbn);
        return isbnField.getText().toString();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    //récupère les infos sur google books
    class FetchBookTask extends AsyncTask<String, Void, BookModel> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SubmitBookActivity.this);
            progressDialog.setMessage("recherche dans la base de données");
            progressDialog.show();
        }

        @Override
        protected BookModel doInBackground(String... params) {
            final String isbn = params[0];
            try {
                return new BookLookupService().fetchBookByISBN(isbn);
            } catch (Exception e) {
                Log.e("fetchBookByISBN", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(BookModel book) {
            try {
                if (book != null) {
                    //showMessage("Got book: " + book.getTitle());
                    titleName.setText(book.getTitle());
                    if (book.getDescription() != null || book.getAuthors() != null || book.getImageLinks().getThumbnail() != null) {
                        lastName.setText(book.getAuthors().toString());
                        resum.setText(book.getDescription().toString());
                        mImageBookVisible.setAdjustViewBounds(true);
                        Picasso.with(SubmitBookActivity.this)
                                .load(book.getImageLinks().getThumbnail())
                                .error(R.drawable.account_icon)
                                .into(mImageBookVisible);

                    }
                } else {
                    showMessage("Ce livre n'ai pas dans la base de données");
                }
            } catch (Exception e) {
                Log.e("Tag", "====>image");
            }
            progressDialog.dismiss();
        }
    }
}
