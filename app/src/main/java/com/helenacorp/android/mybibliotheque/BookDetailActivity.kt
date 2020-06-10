package com.helenacorp.android.mybibliotheque

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
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
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.helenacorp.android.mybibliotheque.BookDetailActivity
import com.helenacorp.android.mybibliotheque.Controllers.Activities.AccountActivity
import com.helenacorp.android.mybibliotheque.model.BookModel
import java.util.*

class BookDetailActivity : AppCompatActivity(), OnOffsetChangedListener {
    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true
    private var appbar: AppBarLayout? = null
    private var collapsing: CollapsingToolbarLayout? = null
    private val couv: ImageView? = null
    private var arrow: ImageView? = null
    private var title: TextView? = null
    private var name: TextView? = null
    private var category: TextView? = null
    private var resume: TextView? = null
    private var title_two: TextView? = null
    private var ratingBar: RatingBar? = null
    var constraintLayout: ConstraintLayout? = null
    private val framelayoutTitle: FrameLayout? = null
    private var linearlayoutTitle: LinearLayoutCompat? = null
    private var toolbar: Toolbar? = null
    private var edit_detail: FloatingActionButton? = null
    private var mAuth: FirebaseAuth? = null
    private var mUser: FirebaseUser? = null
    private var ref: DatabaseReference? = null
    private var key: String? = null
    private val bookModel: BookModel? = null

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_book_detail)
        arrow = findViewById(R.id.arrow_detail)
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
        //retrieve extra in bundle
        val bundle = intent.extras
        category!!.text = bundle!!.getString("category")
        title!!.text = bundle.getString("title")
        title_two!!.text = bundle.getString("title")
        resume!!.text = bundle.getString("info")
        resume!!.movementMethod = ScrollingMovementMethod()
        name!!.text = bundle.getString("lastnameAutor")
        ratingBar!!.rating = bundle.getFloat("rating")
        // final String url = bundle.getString("imageUrl");
        //retrieve key's child of books node
        key = bundle.getString("bookid")

        //   Glide.with(this).load(url).into(couv);
        //   Log.e("BookDetail ", "image url : "+url);
        toolbar!!.title = ""
        appbar!!.addOnOffsetChangedListener(this)
        setSupportActionBar(toolbar)
        startAlphaAnimation(title_two, 0, View.INVISIBLE)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.currentUser
        ref = FirebaseDatabase.getInstance().getReference("users").child(mUser!!.uid).child("books")
        edit_detail!!.setOnClickListener(View.OnClickListener {
            val layoutInflater = LayoutInflater.from(this@BookDetailActivity)
            val view1 = layoutInflater.inflate(R.layout.dialog_detail, null)
            val alertD = AlertDialog.Builder(this@BookDetailActivity)
            alertD.setView(view1)
            val resume_dialog = view1.findViewById<EditText>(R.id.resum_dialog)
            resume_dialog.setText(resume!!.getText())
            val head = findViewById<ImageView>(R.id.head_dialog)
            val headTxt = view1.findViewById<TextView>(R.id.title_dialog)
            headTxt.text = title!!.getText()
            alertD.setPositiveButton("Enregistrer\nles modifs") { dialogInterface, i ->
                //retrieve resume , change it, save in view and upgrade in firebase
                //pass de string key as child in datareference
                val infoDb = ref!!.child(key!!)
                val updateResum = resume_dialog.text.toString()
                resume!!.setText(updateResum)
                val map: MutableMap<String, Any> = HashMap()
                map["info"] = updateResum
                infoDb.updateChildren(map)
                Toast.makeText(this@BookDetailActivity, "RE-enregistré", Toast.LENGTH_SHORT).show()
            }
            alertD.setNegativeButton("Quitter") { dialogInterface, i -> dialogInterface.cancel() }
            val dialog = alertD.create()
            dialog.show()
        })
        arrow!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@BookDetailActivity, AccountActivity::class.java)
            startActivity(intent)
        })
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
}