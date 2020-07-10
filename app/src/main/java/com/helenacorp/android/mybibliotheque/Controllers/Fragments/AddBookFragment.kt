package com.helenacorp.android.mybibliotheque.Controllers.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.helenacorp.android.mybibliotheque.R
import com.helenacorp.android.mybibliotheque.SimpleScannerActivity
import com.helenacorp.android.mybibliotheque.model.Book
import com.helenacorp.android.mybibliotheque.model.BookModel
import com.helenacorp.android.mybibliotheque.model.User
import com.helenacorp.android.mybibliotheque.service.BookLookupService
import kotlinx.android.synthetic.main.fragment_account.*
import retrofit2.http.Url
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
private const val TAG = "AddBookFragment"

class AddBookFragment : Fragment(), View.OnClickListener, BookLookupService.Callbacks {

    private var mClss: Class<*>? = null
    private var titleName: EditText? = null
    private var author: EditText? = null
    private var category: EditText? = null
    private var isbn: EditText? = null
    private var editeur:EditText? = null
    private var pathCover: String? = null
    private var btnClean: Button? = null
    private var btnAdd: Button? = null
    private var btnIsbn: Button? = null
    private var btnVerif: Button? = null
    private var ratingBar: RatingBar? = null
    private var resum: TextView? = null
    private var mDB: FirebaseFirestore? = null
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var mImageBook: ImageView? = null
    private var coverUri: String? = null
    private var mImageBookVisible: ImageView? = null
    private var imguri: Uri? = null
    private var pusblisher :String? = null
    // private var isValide:Boolean = true

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_book, container, false)
        titleName = view.findViewById<View>(R.id.title_submit) as EditText
        author = view.findViewById<View>(R.id.autorLastName_submit) as EditText
        category = view.findViewById<View>(R.id.category_submit) as EditText
        editeur = view.findViewById(R.id.submit_publisher)
        resum = view.findViewById(R.id.submit_resum)
        btnClean = view.findViewById<View>(R.id.btn_clean_submit) as Button
        btnAdd = view.findViewById<View>(R.id.btn_add_submit) as Button
        btnVerif = view.findViewById(R.id.btn_verify_isbn_submit)
        mImageBook = view.findViewById<View>(R.id.submit_photoView) as ImageView
        mImageBookVisible = view.findViewById<View>(R.id.submit_viewpic) as ImageView
        btnIsbn = view.findViewById(R.id.btn_isbn)
        isbn = view.findViewById(R.id.isbn)

/*        Toolbar toolbar = (Toolbar) Objects.requireNonNull(getActivity()).findViewById(R.id.activity_account_toolbar);
        toolbar.setBackgroundColor(R.color.white);*/
        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser
        mDB = Firebase.firestore
        ratingBar = view.findViewById<View>(R.id.submit_rating) as RatingBar
        ratingBar!!.numStars
        ratingBar!!.onRatingBarChangeListener = OnRatingBarChangeListener { ratingBar, rating, fromUser ->
          //  Toast.makeText(context, java.lang.Float.toString(rating), Toast.LENGTH_SHORT).show()
        }
        btnAdd!!.setOnClickListener(this)
        btnClean!!.setOnClickListener(this)
        btnVerif!!.setOnClickListener(this)
        btnIsbn!!.setOnClickListener(this)
        mImageBook!!.setOnClickListener(this)

        return view
    }


    //validate label from autor and title
    private fun validation() {
        if (titleName!!.text.length == 0 || author!!.length() == 0 || isbn!!.text.length == 0) {
            val context = context
            val duration = Toast.LENGTH_SHORT
            Toast.makeText(context, R.string.toast_2, duration).show()
        } else {
            isValidationIsbn()
        }
    }

    //if isbn of book exist
    private fun isValidationIsbn() {
        var isbnId = isbn!!.text.toString()
        var isValide = true
        //  val ref = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid).child("books")
        val ref = mDB!!.collection("users").document(user!!.uid).collection("books")
        ref.get().addOnSuccessListener { result ->
            for (document in result) {
                val isbnquery = document.data["isbn"].toString()
                if (isbnquery == isbnId) {
                    isValide = false
                    Toast.makeText(activity,"Ce livre existe déjà dans votre bibliothèque",Toast.LENGTH_SHORT).show()
                    clearAll()
                    break
                }
            }
            if (isValide) {
                sendBook()
                clearAll()
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "Error getting documents: ", exception)
        }

    }

    //compresse la photo sélectionnée pour la couverture et l'envoie sur firebase
    private fun sendBook() {
        Log.e(TAG, " sendBook image uri !  ${sendImageCover()}")
        val cover = sendImageCover()
        val bookModel = BookModel(
                isbn!!.text.toString(),
                titleName!!.text.toString().toLowerCase(),
                author!!.text.toString(),
                category!!.text.toString(),
                isbn!!.text.toString(),
                ratingBar!!.rating,
                cover,
                false,
                false,
                editeur!!.text.toString(),
                resum!!.text.toString(),
                user!!.uid)
        //Save image info in to firebase database
        //keys = name's attribut
        addBook(bookModel)

    }

    private fun addUploadRecordToDb(uri: String) {
        val data = HashMap<String, Any>()
        data["imageUrl"] = uri

        mDB!!.collection("users").document(user!!.uid)
                .collection("books").document()
                .set(data, SetOptions.merge())
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(activity, "Saved to DB", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(activity, "Error saving to DB", Toast.LENGTH_LONG).show()
                }
    }

    private fun convertViewToDrawable(view: View): Bitmap {
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(spec, spec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val b = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight,
                Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        c.translate((-view.scrollX).toFloat(), (-view.scrollY).toFloat())
        view.draw(c)
        return b
    }

    private fun sendImageCover():String {
        //TODO get image from imageview
/*        mImageBookVisible!!.isDrawingCacheEnabled = true
        mImageBookVisible!!.buildDrawingCache()*/
        var userPic:StorageReference? = null
        mImageBookVisible!!.drawingCache
        if (mImageBookVisible!!.drawable != null) {
           // val bitmap = (mImageBookVisible!!.drawable as BitmapDrawable).bitmap
            val bitmap = convertViewToDrawable(mImageBookVisible!!)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)

            val data = baos.toByteArray()
            // val uploadTask = userPic.putBytes(data)
            if (imguri != null) {
                val storageReference = FirebaseStorage.getInstance().reference
                userPic= storageReference!!.child("couvertures/" + user!!.uid + isbn!!.text + ".jpg")
                val uploadTask = userPic.putBytes(data)
                uploadTask.addOnFailureListener() {}
                        .addOnSuccessListener() { task ->
                           // coverUri = task.uploadSessionUri.toString()
                            val img = mDB!!.collection("users").document(user!!.uid)
                                    .collection("book").document().id
                            mDB!!.collection("users").document(user!!.uid)
                                    .collection("book").document(img).update("imageUrl", userPic.toString())
                            // addUploadRecordToDb(downloadUri.toString())
                            Log.e(TAG, " image telechargée ! : $img + coverUri : ${userPic.toString()}")
                        }
            } else {
                Log.e(TAG, "pas d\'image")
               // Toast.makeText(activity, "pas d\'image", Toast.LENGTH_SHORT).show()
            }
        }
        return userPic.toString()
    }

    private fun addBook(model: BookModel) {
        mDB!!.collection("users").document(user!!.uid)
                .collection("books").document().set(model)
        Toast.makeText(activity, "Enregistré!!", Toast.LENGTH_SHORT).show()
    }

    private fun clearEditText(editText: EditText?) {
        editText!!.setText("")
    }

    private fun clearAll(){
        clearEditText(titleName)
        clearEditText(author)
        clearEditText(isbn)
        clearEditText(category)
        clearEditText(editeur)
        resum!!.text = ""
        cleanCouv(mImageBookVisible)
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

    override fun onClick(view: View) {
        if (view === btnVerif) {
            //  new FetchBookTask().execute(getISBN());
            executeHttpRequestWithRetrofit()
        }
        if (view === btnAdd) {
            validation()
            // sendBookcover()
        }
        if (view === btnClean) {
            clearAll()
        }
        if (view === mImageBook) {
            val checkSelfPermission = ContextCompat.checkSelfPermission(activity!!, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
            else{
                shooseToGallery()
            }
        }
        if (view === btnIsbn) {
            launchActivity(SimpleScannerActivity::class.java)
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(activity!!,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_IMAGE_CAPTURE)
    }

    private fun launchActivity(clss: Class<*>) {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this.context!!), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss
            ActivityCompat.requestPermissions(Objects.requireNonNull(this.activity!!), arrayOf(Manifest.permission.CAMERA), ZXING_CAMERA_PERMISSION)
        } else {
            val intent = Intent(context, clss)
            startActivityForResult(intent, 2)
        }
    }

    //ouvre l'activité du scan code bar
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ZXING_CAMERA_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mClss != null) {
                    Log.e(TAG, " image ZXING ! ")
                    val intent = Intent(activity, mClss)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(activity, "Vous devez accepter la permission de la camera pour utiliser le QR Scanner", Toast.LENGTH_SHORT).show()
            }
            REQUEST_IMAGE_CAPTURE -> if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                //permission from popup granted
                shooseToGallery()
            } else {
                Toast.makeText(activity, "Permission refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shooseToGallery() {
      //  val intent = Intent(Intent.ACTION_PICK)
      //  intent.type = "image/*"
      //  startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
       val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //change intent.setType("images/*") by ("*/*")
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> if (resultCode == Activity.RESULT_OK) {
                imguri = data!!.data
                Log.e(TAG, "1 - Cam imguri : $imguri + requestCode = $requestCode")
                try {
                    mImageBookVisible!!.maxWidth = 80
                    mImageBookVisible!!.maxHeight = 80
                    mImageBookVisible!!.adjustViewBounds = true
                    mImageBookVisible!!.scaleType = ImageView.ScaleType.CENTER_CROP
                    val bitmap = BitmapFactory.decodeStream(
                            activity!!.contentResolver.openInputStream(imguri!!))
                    // mImageBookVisible!!.setImageURI(imguri)
                    mImageBookVisible!!.setImageBitmap(bitmap)

                } catch (e: Exception) {
                    Log.e(TAG, "error onActitiResult : $e")
                    e.printStackTrace()
                }
            }
            ZXING_CAMERA_PERMISSION -> if (resultCode == Activity.RESULT_OK) {
                val returnValue = data!!.getStringExtra("barcode")
                Log.e(TAG, "1 - Zxing scan : $resultCode + requestCode = $resultCode")
                isbn!!.setText(returnValue)
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
           // Log.e("AddBookFragment ", "apres book != null " + "TotalItems : " + book!!.totalItems.toString())
            if (book!!.items!![0].volumeInfo!!.industryIdentifiers!![1].identifier == iSBN) {
                if (!book!!.items!![0].volumeInfo!!.title!!.isEmpty() || !book.items!![0].volumeInfo!!.authors!!.isEmpty()) {

                    //  Log.e("AddBookFragment", " totalItems = "+ book.getTotalItems()+" ISBN get : "+book.getItems().get(0).getVolumeInfo()
                    //  .getIndustryIdentifiers().get(0).getIdentifier());
                    titleName!!.setText(book.items!![0].volumeInfo!!.title)
                    author!!.setText(book.items!![0].volumeInfo!!.authors!![0].toString())
                    editeur!!.setText(book.items!![0].volumeInfo!!.publisher)

                    if (book!!.items!![0].volumeInfo!!.description != null) {
                        resum!!.text = book.items!![0].volumeInfo!!.description.toString()
                        if (book!!.items!![0].volumeInfo!!.imageLinks != null) {
                            Glide.with(Objects.requireNonNull(context!!)).load(book.items!![0].volumeInfo!!.imageLinks!!.thumbnail)
                                    .apply(RequestOptions.circleCropTransform()).into(mImageBookVisible!!)
                              Log.e("AddBookFragment ", "img => " + book.items!!.get(0).volumeInfo!!.imageLinks!!.thumbnail);
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
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val PERMISSION_CODE_READ = 1001
        private const val PERMISSION_CODE_WRITE = 1003
        private const val ZXING_CAMERA_PERMISSION = 2

        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): AddBookFragment {
            return AddBookFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}