package com.helenacorp.android.mybibliotheque.Controllers.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.helenacorp.android.mybibliotheque.R
import com.helenacorp.android.mybibliotheque.model.User
import kotlinx.android.synthetic.main.fragment_account.*
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
private const val TAG = "AccountFragment"

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
    private var mAuthListener: AuthStateListener? = null
    private lateinit var user: FirebaseUser
    private lateinit var mFirestore: FirebaseFirestore
    private var imageUri: Uri? = null
    private var bitmap: Bitmap? = null
    private var cloudL: ImageView? = null
    private var cloudM: ImageView? = null
    private var cloudR: ImageView? = null
    private var cloudTranslate: Animation? = null
    private var mDialog: ProgressDialog? = null
    private val drawer: DrawerLayout? = null
    private var btnphotoProfil: Button? = null
    private var btnDeconnect: Button? = null
    private val mAuthListerner: AuthStateListener? = null

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        acc_username = view.findViewById<View>(R.id.user_name) as TextView
        acc_numlist = view.findViewById<View>(R.id.user_numberBooks) as TextView
        userPic = view.findViewById(R.id.user_pic)
        btnphotoProfil = view.findViewById<View>(R.id.user_photo) as Button
        btnDeconnect = view.findViewById(R.id.user_deconnect)
        btnphotoProfil!!.setOnClickListener(this)
        btnDeconnect!!.setOnClickListener(this)
        cloudL = view.findViewById(R.id.user_cloudL)
        cloudM = view.findViewById(R.id.user_cloudM)
        cloudR = view.findViewById(R.id.user_cloudR)
        mDialog = ProgressDialog(activity)
        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser!!
        mFirestore = Firebase.firestore
        // retrieve number of books in listview firebase
        //  mStorageRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid).child("books")
        //  mStorageRef!!.keepSynced(true)
/*        mStorageRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mDialog!!.dismiss()
                nbrBooks = dataSnapshot.childrenCount.toString()
                acc_numlist!!.text = nbrBooks
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })*/
        downloadAvatar()
        // User is signed in
        uID = user!!.uid
        userPseudo = user!!.displayName
        userEmail = user!!.email
        imageUri = user!!.photoUrl
        acc_username!!.text = userPseudo
        //  userPic!!.setImageURI(imageUri)
        Log.e(TAG, "1 - image URI :$imageUri")
        //sharedpreference
/*        sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sp!!.edit()
        if (sp!!.contains(USER_PIC)) {
            Log.e(TAG,"2 - image URI :$imageUri")
            userPic!!.setImageURI(imageUri)
        } else {
            downloadAvatar()
        }
        if (LIST_BOOKS.isEmpty()) {
            sp = this.activity!!.getPreferences(Context.MODE_PRIVATE)
        } else {
            editor.putString(LIST_BOOKS, Context.MODE_PRIVATE.toString())
        }
        editor.apply()*/

        animateCloud(cloudL)
        cloudTranslate!!.startOffset = 500
        animateCloud(cloudM)
        cloudTranslate!!.startOffset = 100
        animateCloud(cloudR)
        // Inflate the layout for this fragment
        return view
    }

    //download and uploadload photoprofil
    private fun downloadAvatar() {
        // Create a storage reference from our app
        val storageRef = firebaseStorage.reference
        storageRef.child("images/${user.uid}.jpg").downloadUrl.addOnSuccessListener { uri ->
            mDialog!!.setMessage("en charge")
            mDialog!!.show()
            Glide.with(activity!!).load(uri).apply(RequestOptions.circleCropTransform()).into(userPic!!)
            mDialog!!.dismiss()
        }.addOnFailureListener {
            mDialog!!.dismiss()
            // Handle any errors
            // Toast.makeText(AccountActivity.this, exception.toString() + "!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private fun uploadImage() {
        Log.e(TAG, "uploadImage - image URI :$imageUri")
       // var downloadUrl: Uri? = null
        if (imageUri != null) {
            val storageReference = firebaseStorage.reference
            val userPicref = storageReference.child("images/$uID.jpg")
            bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(this.activity)!!.contentResolver, imageUri)
            val baos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()
            val uploadTask = userPicref.putBytes(data)
            uploadTask.addOnFailureListener {
                Toast.makeText(activity, "Pas d'image profil", Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener { taskSnapshot ->
                Toast.makeText(activity, "Uploading Done!!!", Toast.LENGTH_SHORT).show()
                updateProfilUser(imageUri!!)
                val downloadUrl  = taskSnapshot.result.toString()
                val users = User(downloadUrl!!)
                mFirestore.collection("users").document(user.uid).set(users)
                Log.e(TAG, "downloadUrl : ${downloadUrl.toString()}")
            }
        } else {
            Toast.makeText(activity, "Faut choisir", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateProfilUser(imagePhoto: Uri) {
        val profileUpdate = UserProfileChangeRequest.Builder()
                .setPhotoUri(imagePhoto)
                .build()
        user.updateProfile(profileUpdate)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                       val p = imagePhoto.path
                        Glide.with(Objects.requireNonNull(activity)!!).load(imagePhoto)
                                .apply(RequestOptions.circleCropTransform()).into(userPic!!)
                        Log.e(TAG, "success update profil")
                    }
                }.addOnFailureListener { e ->
                    Log.e(TAG, "exception", e)
                }
    }

    private fun showFileChooser() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //change intent.setType("images/*") by ("*/*")
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(resultCode, requestCode, data)
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data!!.data
                Log.e(TAG, " onActivityResult image URI :$imageUri")
                try {
                    //getting image from gallery
                    Glide.with(Objects.requireNonNull(activity)!!).load(imageUri)
                            .apply(RequestOptions.circleCropTransform()).into(userPic!!)
                    uploadImage()
                    //  userPic!!.setImageURI(imageUri)
                    //  uploadImage()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "File not found.")
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
        cloudTranslate = AnimationUtils.loadAnimation(activity, R.anim.cloud_right)
        cloudTranslate!!.setAnimationListener(this@AccountFragment)
        imageView!!.startAnimation(cloudTranslate)
    }

    //to repeat animation because setRepeat don't work'
    override fun onAnimationStart(animation: Animation) {}
    override fun onAnimationEnd(animation: Animation) {
        animation.start()
    }

    override fun onAnimationRepeat(animation: Animation) {}

    private fun signOut() {
        mAuth!!.signOut()
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onClick(v: View) {
        if (v === btnphotoProfil) {
            showFileChooser()
        } else if (v === btnDeconnect) {
            try {
                if (user != null) {
                    signOut()
                    activity!!.finish()
                    //  val intent = Intent(activity, MainLoginActivity::class.java)
                    // startActivity(intent)
                    // ActivityCompat.finishAffinity(activity!!)
                    Toast.makeText(activity, "User Sign out!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("AccountActivity", "onClick: Exception " + e.message, e)
            }
        }
    }

    companion object {
        const val LIST_BOOKS = "listItems"
        const val LIST_REQUEST = 0
        private const val PICK_IMAGE_REQUEST = 111
        private const val USER_PIC = "USER_PIC"
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): AccountFragment {
            return AccountFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}