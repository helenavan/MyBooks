package com.helenacorp.android.mybibliotheque.Controllers.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.helenacorp.android.mybibliotheque.MainLoginActivity
import com.helenacorp.android.mybibliotheque.R
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment(), Animation.AnimationListener, View.OnClickListener {
    private val firebaseStorage = FirebaseStorage.getInstance()
    private var sp: SharedPreferences? = null
    private var acc_username: TextView? = null
    private var acc_numlist: TextView? = null
    private val btn_upload: TextView? = null
    private var uID: String? = null
    private var userEmail: String? = null
    private var userPseudo: String? = null
    private var nbrBooks: String? = null
    private var userPic: ImageView? = null
    private val mBar: ProgressBar? = null
    private var mStorageRef: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var imageUri: Uri? = null
    private var bitmap: Bitmap? = null
    private var cloudL: ImageView? = null
    private var cloudM: ImageView? = null
    private var cloudR: ImageView? = null
    private var cloudTranslate: Animation? = null
    private var mDialog: ProgressDialog? = null
    private val drawer: DrawerLayout? = null
    private var photoProfil: Button? = null
    private var btnDeconnect: Button? = null
    private val mAuthListerner: AuthStateListener? = null

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        acc_username = view.findViewById<View>(R.id.user_name) as TextView
        acc_numlist = view.findViewById<View>(R.id.user_numberBooks) as TextView
        userPic = view.findViewById<View>(R.id.user_pic) as ImageView
        photoProfil = view.findViewById<View>(R.id.user_photo) as Button
        btnDeconnect = view.findViewById(R.id.user_deconnect)
        photoProfil!!.setOnClickListener(this)
        btnDeconnect!!.setOnClickListener(this)
        cloudL = view.findViewById(R.id.user_cloudL)
        cloudM = view.findViewById(R.id.user_cloudM)
        cloudR = view.findViewById(R.id.user_cloudR)
        mDialog = ProgressDialog(context)
        mDialog!!.setMessage("en charge")
        mDialog!!.show()
        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser

        // retrieve number of books in listview firebase
        mStorageRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid).child("books")
        mStorageRef!!.keepSynced(true)
        mStorageRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mDialog!!.dismiss()
                nbrBooks = dataSnapshot.childrenCount.toString()
                acc_numlist!!.text = nbrBooks
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        if (user == null) {
            // User is signed out
            Objects.requireNonNull(this.activity)!!.finish()
            val intent = Intent(context, MainLoginActivity::class.java)
            startActivity(intent)
        } else {
            // User is signed in
            uID = user!!.uid
            userPseudo = user!!.displayName
            userEmail = user!!.email
            imageUri = user!!.photoUrl
            acc_username!!.text = userPseudo
            userPic!!.setImageURI(imageUri)
            //sharedpreference
            sp = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = sp!!.edit()
            if (sp!!.contains(USER_PIC)) {
                userPic!!.setImageURI(imageUri)
            } else {
                downloadAvatar()
            }
            if (LIST_BOOKS.isEmpty()) {
                sp = this.activity!!.getPreferences(Context.MODE_PRIVATE)
            } else {
                editor.putString(LIST_BOOKS, Context.MODE_PRIVATE.toString())
            }
            editor.apply()
        }
        animateCloud(cloudL)
        cloudTranslate!!.startOffset = 500
        animateCloud(cloudM)
        cloudTranslate!!.startOffset = 100
        animateCloud(cloudR)
        // Inflate the layout for this fragment
        return view
    }

    //download and uploadload photoprofil
    private fun downloadAvatar(): Boolean {
        // Create a storage reference from our app
        val storageRef = firebaseStorage.reference
        storageRef.child("images/$uID.jpg").downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context!!).load(uri).apply(RequestOptions.circleCropTransform()).into(userPic!!)
        }.addOnFailureListener {
            // Handle any errors
            // Toast.makeText(AccountActivity.this, exception.toString() + "!!!", Toast.LENGTH_SHORT).show();
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun uploadImage() {
        if (imageUri != null) {
            val storageReference = firebaseStorage.reference
            val userPicref = storageReference.child("images/$uID.jpg")
            userPic!!.isDrawingCacheEnabled = true
            userPic!!.buildDrawingCache()
            bitmap = userPic!!.drawingCache
            val baos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()
            val uploadTask = userPicref.putBytes(data)
            uploadTask.addOnFailureListener { Toast.makeText(context, "Pas d'image profil", Toast.LENGTH_SHORT).show() }.addOnSuccessListener { taskSnapshot ->
                Toast.makeText(context, "Uploading Done!!!", Toast.LENGTH_SHORT).show()
                val downloadUrl = taskSnapshot.uploadSessionUri
                Glide.with(Objects.requireNonNull(context)!!).load(downloadUrl)
                        .apply(RequestOptions.circleCropTransform()).into(userPic!!)
            }
        } else {
            Toast.makeText(context, "Faut choisir", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showFileChooser() {
        val intent = Intent()
        //change intent.setType("images/*") by ("*/*")
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(resultCode, requestCode, data)
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data!!.data
                try {
                    //getting image from gallery
                    bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(this.activity)!!.contentResolver, imageUri)
                    userPic!!.setImageBitmap(bitmap)
                    uploadImage()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (requestCode == LIST_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val returnValue = data!!.getStringExtra(LIST_BOOKS)
                acc_numlist!!.text = returnValue
            }
        }
    }

    //to rotate clouds
    private fun animateCloud(imageView: ImageView?) {
        cloudTranslate = AnimationUtils.loadAnimation(context, R.anim.cloud_right)
        cloudTranslate!!.setAnimationListener(this@AccountFragment)
        imageView!!.startAnimation(cloudTranslate)
    }

    //to repeat animation because setRepeat don't work'
    override fun onAnimationStart(animation: Animation) {}
    override fun onAnimationEnd(animation: Animation) {
        animation.start()
    }

    override fun onAnimationRepeat(animation: Animation) {}

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onClick(v: View) {
        if (v === photoProfil) {
            showFileChooser()
            uploadImage()
        } else if (v === btnDeconnect) {
            try {
                if (user != null) {
                    mAuth!!.signOut()
                    val intent = Intent(context, MainLoginActivity::class.java)
                    startActivity(intent)
                    ActivityCompat.finishAffinity(activity!!)
                    Toast.makeText(context, "User Sign out!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("AcoountActivity", "onClick: Exception " + e.message, e)
            }
        }
    }

    companion object {
        const val LIST_BOOKS = "listItems"
        const val LIST_REQUEST = 0
        private const val PICK_IMAGE_REQUEST = 111
        private const val USER_PIC = "USER_PIC"

        @JvmStatic
        fun newInstance(): AccountFragment {
            // Required empty public constructor
            return AccountFragment()
        }
    }
}