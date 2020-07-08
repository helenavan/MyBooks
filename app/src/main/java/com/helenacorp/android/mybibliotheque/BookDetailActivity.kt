package com.helenacorp.android.mybibliotheque

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.CheckBox
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_detail.*

private const val TAG = "BookDetailActivity"

class BookDetailActivity : AppCompatActivity() {

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
    private var editeur: TextView? = null
    private var ratingBar: RatingBar? = null
    private var idBook: String? = null
    private var toolbar: Toolbar? = null
    private var mAuth: FirebaseAuth? = null
    private var mUser: FirebaseUser? = null
    private var prefs: SharedPreferences? = null
    private var mFireStore: FirebaseFirestore? = null
    private var ref: CollectionReference? = null
    private var chkLu: CheckBox? = null
    private var isLu: Boolean = false
    private var chKPrete: CheckBox? = null
    private var isLend: Boolean? = false

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        prefs = applicationContext.getSharedPreferences("book", 0)

        mAuth = Firebase.auth
        mUser = mAuth!!.currentUser
        mFireStore = Firebase.firestore
        ref = mFireStore!!.collection("users")
                .document(mUser!!.uid).collection("books")

        category = findViewById(R.id.category_detail)
        title = findViewById(R.id.title_detail)
        editeur = findViewById(R.id.publisher_detail)
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
        mresume = prefs!!.getString("description", null)
        idBook = prefs!!.getString("idBook", null)
      //  mUrl = prefs!!.getString("urlImage", null)
        mrating = prefs!!.getFloat("rating", 0f)
        isLu = prefs!!.getBoolean("isread", false)
        isLend = prefs!!.getBoolean("islend", false)
        //set values in layout
        category!!.text = mcategory
        title!!.text = mtitle
        editeur!!.text = prefs!!.getString("editeur",null)
        resume!!.text = mresume
        name!!.text = mname
        ratingBar!!.rating = mrating!!
        if (isLend == true) {
            chKPrete!!.isChecked = true
        }
        if (isLu == true) {
            chkLu!!.isChecked = true
        }

        //change value item book
        btn_detail.setOnClickListener {
            prefs!!.edit().putString("description", resume!!.text.toString()).apply()
            val infoBook = prefs!!.getString("description", null)

            resume!!.text = infoBook
            ref!!.document(idBook!!).update("description", resume!!.text.toString())
            ref!!.document(idBook!!).update("category", category!!.text.toString())
            ref!!.document(idBook!!).update("rating", ratingBar!!.rating!!)
            if (chKPrete!!.isChecked) {
                isLend = true
                ref!!.document(idBook!!).update("islend", isLend)
            } else {
                isLend = false
                ref!!.document(idBook!!).update("islend", isLend)
            }
            if (chkLu!!.isChecked) {
                isLu = true
                ref!!.document(idBook!!).update("isread", isLu)
            } else {
                isLu = false
                ref!!.document(idBook!!).update("isread", isLu)
            }
            Toast.makeText(applicationContext,"Sauvegardé!",Toast.LENGTH_SHORT).show()
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