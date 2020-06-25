package com.helenacorp.android.mybibliotheque

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.helenacorp.android.mybibliotheque.model.BookModel
import kotlinx.android.synthetic.main.activity_detail.*

private const val TAG = "BookDetailActivity"

class BookDetailActivity : AppCompatActivity() {
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
    private var toolbar: Toolbar? = null
    private var edit_detail: FloatingActionButton? = null
    private var mAuth: FirebaseAuth? = null
    private var mUser: FirebaseUser? = null
    private var prefs: SharedPreferences? = null
    private var mFireStore: FirebaseFirestore? = null
    private var ref :CollectionReference? = null
    private var chkLu:CheckBox? = null
    private var isLu:Boolean = false
    private var chKPrete:CheckBox? = null
    private var isLend:Boolean? = false

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        prefs = applicationContext.getSharedPreferences("book", 0)

/*        arrow = findViewById(R.id.arrow_detail)
        arrow!!.setOnClickListener {
        //  prefs!!.edit().clear().apply()
        val intent = Intent(this, AccountActivity::class.java)
        intent.putExtra("frag","2")
        startActivity(intent)
        //TODO
    }*/

        mAuth = Firebase.auth
        mUser = mAuth!!.currentUser
        mFireStore = Firebase.firestore
        ref = mFireStore!!.collection("users")
                .document(mUser!!.uid).collection("books")

        edit_detail = findViewById(R.id.edit_detail)
        //  supportPostponeEnterTransition();
        //   val bookItem: BookModel = intent.getParcelableExtra(EXTRA_CAR_ITEM)
        //    couv = findViewById(R.id.pic_detail);
        category = findViewById(R.id.category_detail)
        title = findViewById(R.id.title_detail)
       // title_two = findViewById(R.id.title_item_two)
        resume = findViewById(R.id.info_detail)
        name = findViewById(R.id.author_detail)
        ratingBar = findViewById(R.id.rating_detail)
        chkLu = findViewById(R.id.check_read_detail)
        chKPrete = findViewById(R.id.check_prete_detail)
        toolbar = findViewById<View>(R.id.toolbar_detail) as Toolbar
        //retrieve item's values from sharedpreferences
        mcategory = prefs!!.getString("category", null)
        mtitle = prefs!!.getString("title", null)
        mname = prefs!!.getString("name", null)
        mresume = prefs!!.getString("info", null)
        idBook = prefs!!.getString("idBook",null)
        mUrl = prefs!!.getString("urlImage", null)
        mrating = prefs!!.getFloat("rating", 0f)
        isLu = prefs!!.getBoolean("isread",false)
        isLend = prefs!!.getBoolean("islend",false)
        //set values in layout
        category!!.text = mcategory
        title!!.text = mtitle
        resume!!.text = mresume
       // resume!!.movementMethod = ScrollingMovementMethod()
        name!!.text = mname
      //  title_two!!.text = mname
      //  toolbar!!.title = ""
        ratingBar!!.rating = mrating!!
        if (isLend ==  true){
            chKPrete!!.isChecked = true
        }
        if(isLu == true){
            chkLu!!.isChecked = true
        }

        //change value item
        btn_detail.setOnClickListener{
            prefs!!.edit().putString("info", resume!!.text.toString()).apply()
            val infoBook = prefs!!.getString("info", null)
            //Log.e(TAG, " infoBook : $infoBook")

            resume!!.text = infoBook
            ref!!.document(idBook!!).update("info",resume!!.text.toString())
            ref!!.document(idBook!!).update("category",category!!.text.toString())
            if(chKPrete!!.isChecked) {
                isLend = true
                Log.e(TAG, " is lend : $isLend")
                ref!!.document(idBook!!).update("islend",isLend)
            }else{
                isLend = false
                ref!!.document(idBook!!).update("islend",isLend)
            }
            if(chkLu!!.isChecked){
                isLu = true
                Log.e(TAG, " is read : $isLu")
                ref!!.document(idBook!!).update("isread",isLu)
            }else{
                isLu = false
                ref!!.document(idBook!!).update("isread",isLu)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.e(TAG, "onbackpressed")
    }
}