package com.helenacorp.android.mybibliotheque;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class SubmitBookActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final int ZXING_CAMERA_PERMISSION = 1;

    String result;
    private Class<?> mClss;
    private EditText titleName, firstName, lastName;
    private TextView isbn;
    private Button btnClean, btnAdd, btnIsbn;
    private RatingBar ratingBar;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ImageView mImageBook;
    private Bitmap imageBitmap;
    private Uri imguri;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_book);

        progressBar = (ProgressBar) findViewById(R.id.submit_progressBar);
        titleName = (EditText) findViewById(R.id.title_submit);
        firstName = (EditText) findViewById(R.id.nameAutor_submit);
        lastName = (EditText) findViewById(R.id.autorLastName_submit);
        btnClean = (Button) findViewById(R.id.btn_clean_submit);
        btnAdd = (Button) findViewById(R.id.btn_add_submit);
        mImageBook = (ImageView) findViewById(R.id.submit_photoView);
        btnIsbn = (Button) findViewById(R.id.submit_btn_isbn);
        isbn = (TextView) findViewById(R.id.submit_isbn);


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
        btnIsbn.setOnClickListener(this);
        mImageBook.setOnClickListener(this);

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

  /*  public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(SubmitBookActivity.this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_photo:
                //shooseToCamera();
            default:
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                imguri = data.getData();
                try {

                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imguri);
                    imageCompress(imageBitmap);
                    mImageBook.setImageBitmap(imageBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String returnValue = data.getStringExtra("barcode");
                isbn.setText(returnValue);
            }
            return;
        }
    }

    public void validation() {
        if (titleName.getText().length() == 0 || firstName.length() == 0 || mImageBook.getDrawable() == null) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "Ha! N'avez-vous pas oublié quelque chose?", duration);
            toast.show();
        } else {
            sendBookcover();
        }
    }

    public void imageCompress(Bitmap bitmap) {
        OutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
    }

    public void sendBookcover() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        final String userName = user.getDisplayName();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("books");
        StorageReference userPic = storageReference.child("couvertures/" + titleName.getText().toString() + firstName.getText().toString() + ".jpg");

        userPic.putFile(imguri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //progressBar.setVisibility(View.VISIBLE);
                // Getting image name from EditText and store into string variable.
                BookModel bookModel = new BookModel(titleName.getText().toString().trim(), null, isbn.getText().toString(), firstName.getText().toString(),
                        lastName.getText().toString(), userName, null, ratingBar.getRating(), taskSnapshot.getDownloadUrl().toString());
                //Save image info in to firebase database
                String uploadId = databaseReference.push().getKey();
                databaseReference.child(uploadId).setValue(bookModel);
                Toast.makeText(SubmitBookActivity.this, "Uploading Done!!!", Toast.LENGTH_SHORT).show();

            }
        });
        // progressBar.setVisibility(View.GONE);
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
        if (view == mImageBook) {
            shooseToCamera();
        }
        if (view == btnIsbn) {
            launchSimpleActivity();
            hidePicBarcode(isbn);

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

    public void launchSimpleActivity() {
        launchActivity(SimpleScannerActivity.class);
        Intent intent1 = new Intent(SubmitBookActivity.this, SimpleScannerActivity.class);
        startActivityForResult(intent1, 1);
    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivity(intent);
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
                return;
        }
    }

    public void hidePicBarcode(View view) {
        if (view != null) {
            btnIsbn.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }
    }

}
