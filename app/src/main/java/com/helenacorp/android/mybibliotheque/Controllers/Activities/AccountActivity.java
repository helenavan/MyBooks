package com.helenacorp.android.mybibliotheque.Controllers.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
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
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.AccountFragment;
import com.helenacorp.android.mybibliotheque.R;
import com.helenacorp.android.mybibliotheque.SubmitBookActivity;
import com.helenacorp.android.mybibliotheque.ViewListBooksActivity;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class AccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    private NavigationView navigationView;
    private Toolbar toolbar;

    private Fragment accountFragment;
    //2.identify each fragment with a number
    private static final int FRAGMENT_ACCOUNT = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.showFirstFragment();

    }

    @Override
    public void onBackPressed() {
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch(id){
            case R.id.nav_myAccount :
                this.showFragment(FRAGMENT_ACCOUNT);
                break;
        }
        this.drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //configure Toolbar
    private void configureToolBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.toolbar = (Toolbar) findViewById(R.id.activity_account_toolbar);
        }
        setSupportActionBar(toolbar);
    }

    //configure DrawerLayout
    private void configureDrawerLayout(){
        this.drawer = (DrawerLayout) findViewById(R.id.constraint);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.drawer_open,
                R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    //configure NavigationView
    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.activity_account_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //FRAGMENTS
    //show first fragment when activity is created
    private void showFirstFragment(){
        Fragment visibleFragment =
                getSupportFragmentManager().findFragmentById(R.id.constraint);
        if(visibleFragment == null){
            //1.1.Show News Fragment
            this.showFragment(FRAGMENT_ACCOUNT);
            //1.2. Mark as selected the menu item corresponding to NewsFragment
            this.navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    //show fragment according an Identifier
    private void showFragment(int fragmentIdentifier){
        switch(fragmentIdentifier){
            case FRAGMENT_ACCOUNT :
                this.showAccountFragment();
                break;
        }
    }

    //create each fragment page and show
    private void showAccountFragment(){
        if(this.accountFragment == null) this.accountFragment = AccountFragment.newInstance();
        this.startTransactionFragment(this.accountFragment);
    }

    //generic method that will replace and show a fragment inside a activity framelayout
    private void startTransactionFragment(Fragment fragment){
        if(!fragment.isVisible()){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_account_frame, fragment).commit();
        }
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
}