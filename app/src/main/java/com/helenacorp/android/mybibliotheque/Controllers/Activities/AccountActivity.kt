package com.helenacorp.android.mybibliotheque.Controllers.Activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.helenacorp.android.mybibliotheque.R

class AccountActivity : AppCompatActivity() {
    // private DrawerLayout drawer;
    private val navigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private var sectionsPagerAdapter:SectionsPagerAdapter? = null

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        configureToolBar()
        configureViewPagerAndTabs()
    }

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
        pager.adapter = SectionsPagerAdapter(this,supportFragmentManager)

        // 1 - Get TabLayout from layout
        val tabs = findViewById<View>(R.id.activity_main_tabs) as TabLayout
        // 2 - Glue TabLayout and ViewPager together
        tabs.setupWithViewPager(pager)
        // 3 - Design purpose. Tabs have the same width
        tabs.tabMode = TabLayout.MODE_FIXED

        pager!!.offscreenPageLimit = 3
        pager!!.currentItem = 0
    }

}