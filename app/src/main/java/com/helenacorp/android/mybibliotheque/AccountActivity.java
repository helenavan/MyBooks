package com.helenacorp.android.mybibliotheque;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class AccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Animation.AnimationListener {
    public static final String LIST_BOOKS = "listItems";
    public static final int LIST_REQUEST = 0;
    private static final int PICK_IMAGE_REQUEST = 111;
    private static final String USER_PIC = "USER_PIC";

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private SharedPreferences sp;
    private TextView acc_username, acc_numlist, btn_upload, acc_status;
    private String uID, userEmail, userPseudo, nbrBooks, userStatus;
    private ImageView userPic;
    private ProgressBar mBar;
    private DatabaseReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Uri imageUri;
    private Bitmap bitmap;
    private ImageView cloudL, cloudM, cloudR;
    private Animation cloudTranslate;
    private ProgressDialog mDialog;
    private DrawerLayout drawer;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        acc_username = (TextView) findViewById(R.id.user_name);
        acc_numlist = (TextView) findViewById(R.id.user_numberBooks);
        acc_status = findViewById(R.id.user_status);
        userPic = (ImageView) findViewById(R.id.user_pic);
        cloudL = findViewById(R.id.user_cloudL);
        cloudM = findViewById(R.id.user_cloudM);
        cloudR = findViewById(R.id.user_cloudR);

        mDialog = new ProgressDialog(AccountActivity.this);
        mDialog.setMessage("charge");
        mDialog.show();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // retrieve number of books in listview firebase
        mStorageRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        mStorageRef.keepSynced(true);

        mStorageRef.child("books").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDialog.dismiss();
                nbrBooks = String.valueOf(dataSnapshot.getChildrenCount());
                acc_numlist.setText(nbrBooks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //to fetch user's status
        getStatus();

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
            userPic.setImageURI(imageUri);
            //sharedpreference
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sp.edit();

            if (sp.contains(USER_PIC)) {
                userPic.setImageURI(imageUri);
            } else {
                downloadAvatar();
            }
            if (LIST_BOOKS.isEmpty()) {
                sp = getPreferences(MODE_PRIVATE);
            } else {
                editor.putString(LIST_BOOKS, String.valueOf(MODE_PRIVATE));
            }
            editor.apply();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        animateCloud(cloudL);
        cloudTranslate.setStartOffset(500);
        animateCloud(cloudM);
        cloudTranslate.setStartOffset(100);
        animateCloud(cloudR);
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
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
     /*   if (id == R.id.setting) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_mylist) {
            Intent intent = new Intent(AccountActivity.this, ViewListBooksActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            showFileChooser();
        } else if (id == R.id.nav_countBooks) {
            displayListBooks();
        } else if (id == R.id.nav_chat){
            Intent i = new Intent(AccountActivity.this, UserList.class);
            startActivity(i);
        } else if (id ==R.id.nav_status){
            setDialogue();
            hidKeyboard();
        } else if(id == R.id.nav_friends){
            Intent i = new Intent(AccountActivity.this, FriendsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_disconnect) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(AccountActivity.this, MainLoginActivity.class);
            startActivity(i);
        }
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(resultCode, requestCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                try {
                    //getting image from gallery
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    userPic.setImageBitmap(bitmap);
                    uploadImage();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == LIST_REQUEST) {
            if (resultCode == RESULT_OK) {
                String returnValue = data.getStringExtra(LIST_BOOKS);
                acc_numlist.setText(returnValue);
            }
        }
    }

    //download and uploadload photoprofil
    private boolean downloadAvatar() {
        // Create a storage reference from our app
        StorageReference storageRef = firebaseStorage.getReference();

        storageRef.child("images/" + uID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSuccess(final Uri uri) {
                final Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.WHITE)
                        .borderWidthDp(3)
                        .cornerRadiusDp(20)
                        .oval(true)
                        .build();
               // Picasso.with(AccountActivity.this).load(uri).fit().transform(transformation).into(userPic);
                Picasso.with(AccountActivity.this).load(uri).networkPolicy(NetworkPolicy.OFFLINE).fit().transform(transformation).into(userPic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(AccountActivity.this).load(uri).fit().transform(transformation).into(userPic);
                    }
                });
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                // Toast.makeText(AccountActivity.this, exception.toString() + "!!!", Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }

    private void uploadImage() {

        if (imageUri != null) {
            final DatabaseReference image_profil = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            StorageReference storageReference = firebaseStorage.getReference();
            final StorageReference userPicref = storageReference.child("images/" + uID + ".jpg");
            userPic.setDrawingCacheEnabled(true);
            userPic.buildDrawingCache();
            bitmap = userPic.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = userPicref.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AccountActivity.this, "Pas d'image profil", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> downloadUrl = userPicref.getDownloadUrl();
                    final String download_url= userPicref.getDownloadUrl().toString();
                    //créer un attribut image dans user firebase
                    image_profil.child("picChatUser").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Transformation transformation = new RoundedTransformationBuilder()
                                        .borderColor(Color.WHITE)
                                        .borderWidthDp(3)
                                        .cornerRadiusDp(55)
                                        .oval(false)
                                        .build();
                                Picasso.with(AccountActivity.this)
                                        .load(download_url)
                                        .placeholder(R.drawable.ic_face_blue_grey_800_24dp)
                                        .fit().transform(transformation)
                                        .into(userPic);
                                Toast.makeText(AccountActivity.this, "Image chargée!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    Log.d("downloadUrl-->", "" + downloadUrl);

                }
            });
        } else {

            Toast.makeText(AccountActivity.this, "Faut choisir", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        //change intent.setType("images/*") by ("*/*")
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void displayListBooks() {
        //retrieve count of books from listview
        Intent intent2 = new Intent(AccountActivity.this, SubmitBookActivity.class);
        startActivity(intent2);
      /*  Intent extras = this.getIntent();
        if(extras != null){
            String values = extras.getStringExtra("list");
            acc_numlist.setText(values);
        }*/

    }
    //to rotate clouds
    private void animateCloud(ImageView imageView){
        cloudTranslate =  AnimationUtils.loadAnimation(getApplicationContext(),R.anim.cloud_right);
        cloudTranslate.setAnimationListener(AccountActivity.this);
        imageView.startAnimation(cloudTranslate);
    }

    //to repeat animation because setRepeat don't work'
    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        animation.start();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void setDialogue(){
        LayoutInflater layoutInflater = LayoutInflater.from(AccountActivity.this);
        View view1 = layoutInflater.inflate(R.layout.dialog_status, null);
        final EditText statusEdit =(EditText) view1.findViewById(R.id.edit_status_dialog);
        final AlertDialog.Builder alertD = new AlertDialog.Builder(AccountActivity.this);
        alertD.setView(view1);
        alertD.setCancelable(false);

        alertD.setPositiveButton(R.string.btn_add,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // Toast.makeText(AccountActivity.this, "testDialogue", Toast.LENGTH_SHORT).show();
                DatabaseReference databaseReference = mStorageRef.child("status");
                String status = statusEdit.getText().toString();
                databaseReference.setValue(status);
            }
        });
        alertD.setNegativeButton(R.string.acc_quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = alertD.create();
        dialog.show();
    }

    public void hidKeyboard(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void getStatus(){
        mStorageRef.child("status").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userStatus = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                acc_status.setText(userStatus);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
