package com.helenacorp.android.mybibliotheque;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnlist, btndisconnect;
    private TextView acc_username, acc_numlist;
    private String uID, userEmail, userPseudo;
    private ImageView userPic;
    private ProgressBar mBar;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        acc_username = (TextView) findViewById(R.id.user_name);
        acc_numlist = (TextView) findViewById(R.id.user_numberBooks);
        btnlist = (Button) findViewById(R.id.user_btnlist);
        btnlist.setOnClickListener(this);
        btndisconnect = (Button) findViewById(R.id.user_disconnect);
        btndisconnect.setOnClickListener(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            // User is signed out
            finish();
            Intent intent = new Intent(AccountActivity.this, MainLoginActivity.class);
            startActivity(intent);


        } else {
            // User is signed in
            uID = user.getUid();
            userPseudo = user.getDisplayName();
            userEmail = user.getEmail();
            imageUri = user.getPhotoUrl();
            acc_username.setText(userPseudo);
            //photoProfil.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(resultCode, requestCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            //photoProfil.setImageURI(imageUri);
            try {
                //getting image from gallery
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                //Setting image to ImageView
                //photoProfil.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnlist) {
            Intent intent = new Intent(AccountActivity.this, ViewListBooksActivity.class);
            startActivity(intent);
        }
        if (view == btndisconnect) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(AccountActivity.this, MainLoginActivity.class);
            startActivity(i);
        }


    }
    /*
    //download and uploadload photoprofil
    private void downloadAvatar() {
        // Create a storage reference from our app
        StorageReference storageRef = firebaseStorage.getReference();

        storageRef.child("images/" + uId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                progressDialog.dismiss();
                Glide.with(AccountActivity.this)
                        .load(uri)
                        .into(photoProfil);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                progressDialog.dismiss();
                Toast.makeText(AccountActivity.this, exception.toString() + "!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage() {

        if (imageUri != null) {


            StorageReference storageReference = firebaseStorage.getReference();
            StorageReference userPicref = storageReference.child("images/" + uId + ".jpg");
            photoProfil.setDrawingCacheEnabled(true);
            photoProfil.buildDrawingCache();
            Bitmap bitmap = photoProfil.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = userPicref.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AccountActivity.this, "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(AccountActivity.this, "Uploading Done!!!", Toast.LENGTH_SHORT).show();
                    Glide.with(AccountActivity.this)
                            .load(downloadUrl)
                            .into(photoProfil);
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(AccountActivity.this, "Faut choisir", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }*/
}
