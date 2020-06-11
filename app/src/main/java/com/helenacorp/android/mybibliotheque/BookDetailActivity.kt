package com.helenacorp.android.mybibliotheque

import android.annotation.TargetApi
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.helenacorp.android.mybibliotheque.Controllers.Activities.AccountActivity
import com.helenacorp.android.mybibliotheque.Controllers.Activities.SectionsPagerAdapter
import com.helenacorp.android.mybibliotheque.Controllers.Fragments.ListBooksFragment
import com.helenacorp.android.mybibliotheque.model.BookModel
import kotlinx.android.synthetic.main.dialog_detail.*
import kotlinx.android.synthetic.main.dialog_detail.view.*

private const val TAG = "BookDetailActivity"

class BookDetailActivity : AppCompatActivity(), OnOffsetChangedListener {
    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true
    private var appbar: AppBarLayout? = null
    private var collapsing: CollapsingToolbarLayout? = null
    private val couv: ImageView? = null
    private var arrow: ImageView? = null
    private var title: TextView? = null
    private var mtitle: String? = null
    private var name: TextView? = null
    private var mname: String? = null
    private var category: TextView? = null
    private var mcategory: String? = null
    private var resume: TextView? = null
    private var mresume: String? = null
    private var mUrl: String? = null
    private var mrating: Float? = null
    private var title_two: TextView? = null
    private var ratingBar: RatingBar? = null
    private var idBook:String? = null
    var constraintLayout: ConstraintLayout? = null
    private val framelayoutTitle: FrameLayout? = null
    private var linearlayoutTitle: LinearLayoutCompat? = null
    private var toolbar: Toolbar? = null
    private var edit_detail: FloatingActionButton? = null
    private var mAuth: FirebaseAuth? = null
    private var mUser: FirebaseUser? = null
    private var key: String? = null
    private val bookModel: BookModel? = null
    private var prefs: SharedPreferences? = null
    private var pager: ViewPager? = null
    private var mFireStore: FirebaseFirestore? = null
    private var ref :CollectionReference? = null

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        prefs = applicationContext.getSharedPreferences("book", 0)

        arrow = findViewById(R.id.arrow_detail)
        arrow!!.setOnClickListener {
          //  prefs!!.edit().clear().apply()
            val intent = Intent(this, AccountActivity::class.java)
            intent.putExtra("frag","2")
            startActivity(intent)
            //TODO
        }

        mAuth = Firebase.auth
        mUser = mAuth!!.currentUser
        mFireStore = Firebase.firestore
        ref = mFireStore!!.collection("users")
                .document(mUser!!.uid).collection("books")

        edit_detail = findViewById(R.id.edit_detail)
        //  supportPostponeEnterTransition();
        //   val bookItem: BookModel = intent.getParcelableExtra(EXTRA_CAR_ITEM)
        //    couv = findViewById(R.id.pic_detail);
        category = findViewById(R.id.category_item)
        title = findViewById(R.id.title_item)
        title_two = findViewById(R.id.title_item_two)
        resume = findViewById(R.id.resum_item)
        name = findViewById(R.id.autorLastName_item)
        ratingBar = findViewById(R.id.ratingbar)
        constraintLayout = findViewById(R.id.container)
        linearlayoutTitle = findViewById<View>(R.id.linearlayout_title) as LinearLayoutCompat
        collapsing = findViewById<View>(R.id.collapsing) as CollapsingToolbarLayout
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        appbar = findViewById<View>(R.id.app_bar) as AppBarLayout
        //retrieve item's values from sharedpreferences
        mcategory = prefs!!.getString("category", null)
        mtitle = prefs!!.getString("title", null)
        mname = prefs!!.getString("name", null)
        mresume = prefs!!.getString("info", null)
        idBook = prefs!!.getString("idBook",null)
        Log.e(TAG, " idBook : $idBook")
        mUrl = prefs!!.getString("urlImage", null)
        mrating = prefs!!.getFloat("rating", 0f)
        Log.e(TAG, " titlte from shared : $mtitle")
        //set values in layout
        category!!.text = mcategory
        title!!.text = mtitle
        resume!!.text = mresume
        resume!!.movementMethod = ScrollingMovementMethod()
        name!!.text = mname
        title_two!!.text = mname
        toolbar!!.title = ""
        ratingBar!!.rating = mrating!!
        //actionBar
        appbar!!.addOnOffsetChangedListener(this)
        setSupportActionBar(toolbar)
        startAlphaAnimation(title_two, 0, View.INVISIBLE)
        //change value item
        edit_detail!!.setOnClickListener {
            val layoutInflater = LayoutInflater.from(this@BookDetailActivity)
            val view1 = layoutInflater.inflate(R.layout.dialog_detail, null)
            val alertD = AlertDialog.Builder(this@BookDetailActivity)
            alertD.setView(view1)
            view1.title_dialog.text = mtitle
          //  val resume_dialog = view1.findViewById<EditText>(R.id.resum_dialog)
            if (!mresume.isNullOrEmpty()) {
               view1.resum_dialog.setText(mresume!!)
            }

            alertD.setPositiveButton("Enregistrer") { dialogInterface, i ->
                prefs!!.edit().putString("info", view1.resum_dialog!!.text.toString()).apply()
                val infoBook = prefs!!.getString("info", null)
                Log.e(TAG, " infoBook : $infoBook")
                resume!!.text = infoBook
                ref!!.document(idBook!!).update("info",infoBook)
            }
            alertD.setNegativeButton("Quitter") { dialogInterface, i -> dialogInterface.cancel() }
            alertD.create()
            alertD.show()
        }
    }

    private fun configureViewPagerAndTabs() {
        //Get ViewPager from layout
        val pager = findViewById<View>(R.id.activity_main_viewpager) as ViewPager
        //Set Adapter PageAdapter and glue it together
        pager.adapter = SectionsPagerAdapter(this, supportFragmentManager)

        // 1 - Get TabLayout from layout
        val tabs = findViewById<View>(R.id.activity_main_tabs) as TabLayout
        // 2 - Glue TabLayout and ViewPager together
        tabs.setupWithViewPager(pager)
        // 3 - Design purpose. Tabs have the same width
        tabs.tabMode = TabLayout.MODE_FIXED

        pager!!.currentItem = (pager.currentItem) - 1
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, offset: Int) {
        val maxScroll = appBarLayout.totalScrollRange
        val percentage = Math.abs(offset).toFloat() / maxScroll.toFloat()
        handleAlphaOnTitle(percentage)
        handleToolbarTitleVisibility(percentage)
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(title_two, ALPHA_ANIMATIONS_DURATION.toLong(), View.VISIBLE)
                mIsTheTitleVisible = true
            }
        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(title_two, ALPHA_ANIMATIONS_DURATION.toLong(), View.INVISIBLE)
                mIsTheTitleVisible = false
            }
        }
    }

    private fun handleAlphaOnTitle(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION.toLong(), View.INVISIBLE)
                mIsTheTitleContainerVisible = false
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION.toLong(), View.VISIBLE)
                mIsTheTitleContainerVisible = true
            }
        }
    }

    companion object {
        const val EXTRA_CAR_ITEM = "com.helenacorp.android.mybibliotheque"
        private const val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f
        private const val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f
        private const val ALPHA_ANIMATIONS_DURATION = 200
        fun EncodeString(string: String): String {
            return string.replace(".", ",")
        }

        fun DecodeString(string: String): String {
            return string.replace(",", ".")
        }

        fun startAlphaAnimation(v: View?, duration: Long, visibility: Int) {
            val alphaAnimation: AlphaAnimation
            alphaAnimation = if (visibility == View.VISIBLE) AlphaAnimation(0f, 1f) else AlphaAnimation(1f, 0f)
            alphaAnimation.duration = duration
            alphaAnimation.fillAfter = true
            v!!.startAnimation(alphaAnimation)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.e(TAG, "onbackpressed")
    }
}