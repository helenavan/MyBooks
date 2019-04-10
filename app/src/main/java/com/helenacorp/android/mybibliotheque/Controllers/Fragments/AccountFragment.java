package com.helenacorp.android.mybibliotheque.Controllers.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.helenacorp.android.mybibliotheque.MainLoginActivity;
import com.helenacorp.android.mybibliotheque.R;
import com.helenacorp.android.mybibliotheque.SubmitBookActivity;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements Animation.AnimationListener {
    public static final String LIST_BOOKS = "listItems";
    public static final int LIST_REQUEST = 0;
    private static final int PICK_IMAGE_REQUEST = 111;
    private static final String USER_PIC = "USER_PIC";

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private SharedPreferences sp;
    private TextView acc_username, acc_numlist, btn_upload;
    private String uID, userEmail, userPseudo, nbrBooks;
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

    public static AccountFragment newInstance() {
        // Required empty public constructor
        return(new AccountFragment());
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        acc_username = (TextView) view.findViewById(R.id.user_name);
        acc_numlist = (TextView) view.findViewById(R.id.user_numberBooks);
        userPic = (ImageView) view.findViewById(R.id.user_pic);
        cloudL = view.findViewById(R.id.user_cloudL);
        cloudM = view.findViewById(R.id.user_cloudM);
        cloudR = view.findViewById(R.id.user_cloudR);

        mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("en charge");
        mDialog.show();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // retrieve number of books in listview firebase
        mStorageRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("books");
        mStorageRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

        if (user == null) {
            // User is signed out
            this.getActivity().finish();
            Intent intent = new Intent(getContext(), MainLoginActivity.class);
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
            sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sp.edit();

            if (sp.contains(USER_PIC)) {
                userPic.setImageURI(imageUri);
            } else {
                downloadAvatar();
            }
            if (LIST_BOOKS.isEmpty()) {
                sp = this.getActivity().getPreferences(MODE_PRIVATE);
            } else {
                editor.putString(LIST_BOOKS, String.valueOf(MODE_PRIVATE));
            }
            editor.apply();
        }

/*        animateCloud(cloudL);
        cloudTranslate.setStartOffset(500);
        animateCloud(cloudM);
        cloudTranslate.setStartOffset(100);
        animateCloud(cloudR);*/
        // Inflate the layout for this fragment
        return view;
    }

    //download and uploadload photoprofil
    private boolean downloadAvatar() {
        // Create a storage reference from our app
        StorageReference storageRef = firebaseStorage.getReference();

        storageRef.child("images/" + uID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSuccess(Uri uri) {
                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.WHITE)
                        .borderWidthDp(3)
                        .cornerRadiusDp(20)
                        .oval(true)
                        .build();
                Picasso.get().load(uri).fit().transform(transformation).into(userPic);
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

            StorageReference storageReference = firebaseStorage.getReference();
            StorageReference userPicref = storageReference.child("images/" + uID + ".jpg");
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
                    Toast.makeText(getContext(), "Pas d'image profil", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Uploading Done!!!", Toast.LENGTH_SHORT).show();
                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                    Transformation transformation = new RoundedTransformationBuilder()
                            .borderColor(Color.WHITE)
                            .borderWidthDp(3)
                            .cornerRadiusDp(55)
                            .oval(false)
                            .build();
                    Picasso.get()
                            .load(downloadUrl)
                            .fit().transform(transformation)
                            .into(userPic);
                    Log.d("downloadUrl-->", "" + downloadUrl);

                }
            });
        } else {

            Toast.makeText(getContext(), "Faut choisir", Toast.LENGTH_SHORT).show();
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
        Intent intent2 = new Intent(getContext(), SubmitBookActivity.class);
        startActivity(intent2);
      /*  Intent extras = this.getIntent();
        if(extras != null){
            String values = extras.getStringExtra("list");
            acc_numlist.setText(values);
        }*/

    }
    //to rotate clouds
    private void animateCloud(ImageView imageView){
        cloudTranslate =  AnimationUtils.loadAnimation(getContext(),R.anim.cloud_right);
        cloudTranslate.setAnimationListener(AccountFragment.this);
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
}
