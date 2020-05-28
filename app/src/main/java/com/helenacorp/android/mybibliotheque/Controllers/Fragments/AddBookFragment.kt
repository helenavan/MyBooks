package com.helenacorp.android.mybibliotheque.Controllers.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.helenacorp.android.mybibliotheque.R
import com.helenacorp.android.mybibliotheque.SimpleScannerActivity
import com.helenacorp.android.mybibliotheque.model.Book
import com.helenacorp.android.mybibliotheque.model.BookModel
import com.helenacorp.android.mybibliotheque.service.BookLookupService
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AddBookFragment : Fragment(), View.OnClickListener, BookLookupService.Callbacks {
    var result: String? = null
    private var mClss: Class<*>? = null
    private var titleName: EditText? = null
    private var author: EditText? = null
    private var category: EditText? = null
    private var isbn: EditText? = null
    private val isbnId: String? = null
    private var btnClean: Button? = null
    private var btnAdd: Button? = null
    private var btnIsbn: Button? = null
    private var btnVerif: Button? = null
    private var ratingBar: RatingBar? = null
    private var resum: TextView? = null
    private var mDB:FirebaseFirestore? = null
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var mImageBook: ImageView? = null
    private var mImageBookVisible: ImageView? = null
    private var imguri: Uri? = null
    private var databaseReference: DatabaseReference? = null
    private val glide: RequestManager? = null
    private val bookModel: BookModel? = null

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_book, container, false)
        titleName = view.findViewById<View>(R.id.title_submit) as EditText
        author = view.findViewById<View>(R.id.autorLastName_submit) as EditText
        category = view.findViewById<View>(R.id.category_submit) as EditText
        resum = view.findViewById(R.id.submit_resum)
        btnClean = view.findViewById<View>(R.id.btn_clean_submit) as Button
        btnAdd = view.findViewById<View>(R.id.btn_add_submit) as Button
        btnVerif = view.findViewById(R.id.btn_verify_isbn_submit)
        mImageBook = view.findViewById<View>(R.id.submit_photoView) as ImageView
        mImageBookVisible = view.findViewById<View>(R.id.submit_viewpic) as ImageView
        btnIsbn = view.findViewById<View>(R.id.submit_btn_isbn) as Button
        isbn = view.findViewById(R.id.isbn)

/*        Toolbar toolbar = (Toolbar) Objects.requireNonNull(getActivity()).findViewById(R.id.activity_account_toolbar);
        toolbar.setBackgroundColor(R.color.white);*/
        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser
        mDB = Firebase.firestore
        ratingBar = view.findViewById<View>(R.id.submit_rating) as RatingBar
        ratingBar!!.numStars
        ratingBar!!.onRatingBarChangeListener = OnRatingBarChangeListener { ratingBar, rating, fromUser -> Toast.makeText(context, java.lang.Float.toString(rating), Toast.LENGTH_SHORT).show() }
        btnAdd!!.setOnClickListener(this)
        btnClean!!.setOnClickListener(this)
        btnVerif!!.setOnClickListener(this)
        btnIsbn!!.setOnClickListener(this)
        mImageBook!!.setOnClickListener(this)
        return view
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                imguri = data!!.data
                try {
                    val imageBitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(activity!!).contentResolver, imguri)
                    // mImageBook.setVisibility(View.INVISIBLE);
                    mImageBookVisible!!.maxWidth = 80
                    mImageBookVisible!!.maxHeight = 80
                    mImageBookVisible!!.adjustViewBounds = true
                    mImageBookVisible!!.scaleType = ImageView.ScaleType.CENTER_CROP
                    mImageBookVisible!!.setImageBitmap(imageBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val returnValue = data!!.getStringExtra("barcode")
                isbn!!.setText(returnValue)
            }
        }
    }

    //validate label from autor and title
    private fun validation() {
        if (titleName!!.text.length == 0 || author!!.length() == 0 || validationIsbn()) {
            if (titleName!!.text.length == 0 || author!!.length() == 0) {
                val context = context
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, R.string.toast_2, duration)
                toast.show()
            }
        } else {
            sendBookcover()
            // if (mImageBookVisible != null) sendBookcover()
        }
    }

    //if isbn of book exist
    private fun validationIsbn(): Boolean {
        val isbnId = isbn!!.text.toString()
        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser
        //  val ref = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid).child("books")
        val ref = mDB!!.collection("users").document(user!!.uid).collection("books")
        ref.get()
                .addOnSuccessListener { result ->

                    for (document in result) {
                        val isbnquery = document.data["isbn"].toString()
                        if (isbnquery == isbnId) {
                            Toast.makeText(context, R.string.toast, Toast.LENGTH_SHORT).show()
                            break
                        } else {
                            this.sendBookcover()
                        }
                    }

                }.addOnFailureListener { exception ->
                    Log.d("TAG", "Error getting documents: ", exception)
                }

        return true
    }

    //compresse la photo sélectionnée pour la couverture et l'envoie sur firebase
    private fun sendBookcover() {
        val userName = user!!.displayName
        mImageBookVisible!!.isDrawingCacheEnabled = true
        mImageBookVisible!!.buildDrawingCache()
        val bitmap = mImageBookVisible!!.drawingCache
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val storageReference = FirebaseStorage.getInstance().reference
        val userPic = storageReference.child("couvertures/" + user!!.uid + titleName!!.text.toString().trim { it <= ' ' } + ".jpg")
        val data = baos.toByteArray()
        val uploadTask = userPic.putBytes(data)
        val bookModel = BookModel(
                titleName!!.text.toString(),
                author!!.text.toString(),
                category!!.text.toString(),
                isbn!!.text.toString(),
                ratingBar!!.rating,
                userPic.downloadUrl.toString(),
                false,
                false,
                resum!!.text.toString())
        //                //Save image info in to firebase database
        //keys = name's attribut
        addBook(bookModel)
    }

    private fun addBook(model: BookModel) {
        mDB!!.collection("users").document(user!!.uid)
                .collection("books").document().set(model)
        Toast.makeText(context, "Enregistré!!", Toast.LENGTH_SHORT).show()
    }

    private fun clearEditText(editText: EditText?) {
        editText!!.setText("")
    }

    private fun cleanCouv(img: ImageView?) {
        if (img != null) {
            img.setImageResource(R.drawable.clean_image_submit)
            img.maxWidth = 80
            img.maxHeight = 80
            img.adjustViewBounds = true
            img.scaleType = ImageView.ScaleType.CENTER
            // img.getDrawingCache(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onClick(view: View) {
        if (view === btnVerif) {
            //  new FetchBookTask().execute(getISBN());
            executeHttpRequestWithRetrofit()
        }
        if (view === btnAdd) {
            // validation()
            sendBookcover()
        }
        if (view === btnClean) {
            clearEditText(titleName)
            clearEditText(author)
            clearEditText(isbn)
            clearEditText(category)
            resum!!.text = ""
            cleanCouv(mImageBookVisible)
        }
        if (view === mImageBook) {
            shooseToCamera()
        }
        if (view === btnIsbn) {
            launchActivity(SimpleScannerActivity::class.java)
        }
    }

    private fun shooseToCamera() {
        val intent = Intent()
        //change intent.setType("images/*") by ("*/*")
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CAPTURE)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun launchActivity(clss: Class<*>) {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this.context!!), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss
            ActivityCompat.requestPermissions(Objects.requireNonNull(this.activity!!), arrayOf(Manifest.permission.CAMERA), ZXING_CAMERA_PERMISSION)
        } else {
            val intent = Intent(context, clss)
            startActivityForResult(intent, 1)
        }
    }

    //ouvre l'activité du scan code bar
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ZXING_CAMERA_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mClss != null) {
                    val intent = Intent(context, mClss)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(context, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val iSBN: String
        get() {
            val isbnField = Objects.requireNonNull(activity!!).findViewById<View>(R.id.isbn) as EditText
            return isbnField.text.toString()
        }

    private fun showMessage(message: String) {
        Toast.makeText(this.context!!.applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun executeHttpRequestWithRetrofit() {
        // this.updateUIWhenStartingHTTPRequest();
        BookLookupService.fetchBookByISBN(this, iSBN, context)
        Log.e("AddBookFragment", "getISBN from editText : => $iSBN")
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onReponse(book: Book?) {
        if (!iSBN.isEmpty()) {
            Log.e("AddBookFragment ", "apres book != null " + "TotalItems : " + book!!.totalItems.toString())
            if (book.items!![0].volumeInfo!!.industryIdentifiers!![1].identifier == iSBN) {
                if (!book.items!![0].volumeInfo!!.title!!.isEmpty() || !book.items!![0].volumeInfo!!.authors!!.isEmpty()) {

                    //  Log.e("AddBookFragment", " totalItems = "+ book.getTotalItems()+" ISBN get : "+book.getItems().get(0).getVolumeInfo()
                    //  .getIndustryIdentifiers().get(0).getIdentifier());
                    titleName!!.setText(book.items!![0].volumeInfo!!.title)
                    author!!.setText(book.items!![0].volumeInfo!!.authors!![0].toString())
                    if (book.items!![0].volumeInfo!!.description != null) {
                        resum!!.text = book.items!![0].volumeInfo!!.description.toString()
                        if (book.items!![0].volumeInfo!!.imageLinks != null) {
                            Glide.with(Objects.requireNonNull(context!!)).load(book.items!![0].volumeInfo!!.imageLinks!!.thumbnail)
                                    .apply(RequestOptions.circleCropTransform()).into(mImageBookVisible!!)
                            //  Log.e("AddBookFragment ", "img => " + book.getItems().get(0).getVolumeInfo().getImageLinks().getThumbnail());
                        }
                    }
                }
            }
        } else {
            Toast.makeText(context, " isbn non renseigné ", Toast.LENGTH_LONG).show()
        }
    }

    override fun onFailure() {
        Log.e("AddBookFragment ", " failure")
        Toast.makeText(context, "failed", Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 111
        private const val ZXING_CAMERA_PERMISSION = 1

        @JvmStatic
        fun newInstance(): AddBookFragment {
            return AddBookFragment()
        }
    }
}