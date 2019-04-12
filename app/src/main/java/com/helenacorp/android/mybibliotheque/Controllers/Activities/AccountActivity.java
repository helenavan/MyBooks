package com.helenacorp.android.mybibliotheque.Controllers.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.AccountFragment;
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.AddBookFragment;
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.ListBooksFragment;
import com.helenacorp.android.mybibliotheque.MainLoginActivity;
import com.helenacorp.android.mybibliotheque.R;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class AccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ListBooksFragment.OnButtonClickedListener {
    public static final String LIST_BOOKS = "listItems";
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;

    public static final String EXTRA_BUTTON_TAG =
            "com.helenacorp.android.mybibliotheque.Controllers.Activities.AccountActivity.EXTRA_BUTTON_TAG";

    private Fragment accountFragment;
    private Fragment addBookFragment;
    private Fragment listBookFragment;

    //2.identify each fragment with a number
    private static final int FRAGMENT_ACCOUNT = 0;
    private static final int FRAGMENT_ADD = 1;
    private static final int FRAGMENT_LIST = 2;

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
        switch (id) {
            case R.id.nav_myAccount:
                this.showFragment(FRAGMENT_ACCOUNT);
                break;
            case R.id.nav_countBooks:
                this.showFragment(FRAGMENT_ADD);
                break;
            case R.id.nav_mylist:
                this.showFragment(FRAGMENT_LIST);
                break;
            case R.id.nav_disconnect:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(AccountActivity.this, MainLoginActivity.class);
                startActivity(i);
                break;
        }
        this.drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //configure Toolbar
    private void configureToolBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.toolbar = (Toolbar) findViewById(R.id.activity_account_toolbar);
        }
        setSupportActionBar(toolbar);
    }

    //configure DrawerLayout
    private void configureDrawerLayout() {
        this.drawer = (DrawerLayout) findViewById(R.id.constraint);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open,
                R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    //configure NavigationView
    private void configureNavigationView() {
        this.navigationView = (NavigationView) findViewById(R.id.activity_account_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //FRAGMENTS
    //show first fragment when activity is created
    private void showFirstFragment() {
        Fragment visibleFragment =
                getSupportFragmentManager().findFragmentById(R.id.constraint);
        if (visibleFragment == null) {
            //1.1.Show News Fragment
            this.showFragment(FRAGMENT_ACCOUNT);
            //1.2. Mark as selected the menu item corresponding to NewsFragment
            this.navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    //show fragment according an Identifier
    private void showFragment(int fragmentIdentifier) {
        switch (fragmentIdentifier) {
            case FRAGMENT_ACCOUNT:
                this.showAccountFragment();
                break;
            case FRAGMENT_LIST:
                this.showListBookFragment();
                break;
            case FRAGMENT_ADD:
                this.showAddBookFragment();
                break;
        }
    }

    //create each fragment page and show
    private void showAccountFragment() {
        if (this.accountFragment == null) this.accountFragment = AccountFragment.newInstance();
        this.startTransactionFragment(this.accountFragment);
    }

    private void showAddBookFragment() {
        if (this.addBookFragment == null) this.addBookFragment = AddBookFragment.newInstance();
        this.startTransactionFragment(this.addBookFragment);
    }

    private void showListBookFragment() {
        if (this.listBookFragment == null) this.listBookFragment = ListBooksFragment.newInstance();
        this.startTransactionFragment(this.listBookFragment);
    }

    //generic method that will replace and show a fragment inside a activity framelayout
    private void startTransactionFragment(Fragment fragment) {
        if (!fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_account_frame, fragment).commit();
        }
    }

    @Override
    public void onButtonClicked(View view) {
        Log.e("AccountActivity", "onButtonClicked :==>");


    }
}