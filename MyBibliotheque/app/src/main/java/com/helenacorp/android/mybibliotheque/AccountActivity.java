package com.helenacorp.android.mybibliotheque;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
