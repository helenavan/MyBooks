package com.helenacorp.android.mybibliotheque.Controllers.Activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.helenacorp.android.mybibliotheque.R
import com.helenacorp.android.mybibliotheque.UI.PageAdapter

class AccountActivity : AppCompatActivity() {
    // private DrawerLayout drawer;
    private val navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private val btnExit: Button? = null
    private val accountFragment: Fragment? = null
    private val addBookFragment: Fragment? = null
    private val listBookFragment: Fragment? = null

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        configureToolBar()
        configureViewPagerAndTabs()
        // this.configureDrawerLayout();
        // this.configureNavigationView();
        // this.showFirstFragment();
    }

    /*@Override
    public void onBackPressed() {
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/
    //@SuppressWarnings("StatementWithEmptyBody")
    /* public boolean onNavigationItemSelected(MenuItem item) {
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
    }*/
    //configure Toolbar
    private fun configureToolBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar = findViewById<View>(R.id.activity_account_toolbar) as Toolbar
        }
        setSupportActionBar(toolbar)
        toolbar!!.setBackgroundResource(R.color.orangeD)
    }

    private fun configureViewPagerAndTabs() {
        //Get ViewPager from layout
        val pager = findViewById<View>(R.id.activity_main_viewpager) as ViewPager
        //Set Adapter PageAdapter and glue it together
        pager.adapter = PageAdapter(supportFragmentManager)

        // 1 - Get TabLayout from layout
        val tabs = findViewById<View>(R.id.activity_main_tabs) as TabLayout
        // 2 - Glue TabLayout and ViewPager together
        tabs.setupWithViewPager(pager)
        // 3 - Design purpose. Tabs have the same width
        tabs.tabMode = TabLayout.MODE_FIXED
    }

    //configure DrawerLayout
    /* private void configureDrawerLayout() {
        this.drawer = (DrawerLayout) findViewById(R.id.constraint);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open,
                R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }*/
    //configure NavigationView
    /* private void configureNavigationView() {
        this.navigationView = (NavigationView) findViewById(R.id.activity_account_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }*/
    //FRAGMENTS
    //show first fragment when activity is created
    /* private void showFirstFragment() {
        Fragment visibleFragment =
                getSupportFragmentManager().findFragmentById(R.id.constraint);
        if (visibleFragment == null) {
            //1.1.Show News Fragment
            this.showFragment(FRAGMENT_ACCOUNT);
            //1.2. Mark as selected the menu item corresponding to NewsFragment
            this.navigationView.getMenu().getItem(0).setChecked(true);
        }
    }*/
    //show fragment according an Identifier
    /* private void showFragment(int fragmentIdentifier) {
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
    }*/
    //create each fragment page and show
    /* private void showAccountFragment() {
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
    }*/

    companion object {
        const val LIST_BOOKS = "listItems"
        const val EXTRA_BUTTON_TAG = "com.helenacorp.android.mybibliotheque.Controllers.Activities.AccountActivity.EXTRA_BUTTON_TAG"

        //2.identify each fragment with a number
        private const val FRAGMENT_ACCOUNT = 0
        private const val FRAGMENT_ADD = 1
        private const val FRAGMENT_LIST = 2
    }



}