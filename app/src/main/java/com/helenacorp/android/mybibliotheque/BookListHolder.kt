package com.helenacorp.android.mybibliotheque

import android.content.Context
import android.content.Intent
import android.graphics.ColorFilter
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.helenacorp.android.mybibliotheque.model.BookModel

private const val TAG = "BookListHolder"

class BookListHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener, View.OnLongClickListener {

    private var txtTitle: TextView? = null
    private var title: String? = null
    private var txtAutorLastname: TextView? = null
    private var lastname: String? = null
    private var isbnNumber: TextView? = null
    private var isbn: String? = null
    private var resume: TextView? = null
    private var info: String? = null
    private var keyBook: TextView? = null
    private var category: TextView? = null
    private var categorie: String? = null
    private var isread: Boolean = false
    private var isprete: Boolean = false
    private var urlImage: String? = null
    private var ratingBar: RatingBar? = null
    private var mrating: Float? = null
    private var pic: ImageView? = null
    private var ic_lu:ImageView? = null
    private var ic_prete:ImageView? = null
    private var context: Context? = null
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var img_uri: Uri? = null
    private var mFireStore: FirebaseFirestore? = null
    private var storageReference: StorageReference? = null
    // private lateinit var idBook:String
    // private var idBook:String? = null

    init {
        txtTitle = v.findViewById<View>(R.id.title_item) as TextView
        txtAutorLastname = v.findViewById<View>(R.id.autorLastName_item) as TextView
        isbnNumber = v.findViewById<View>(R.id.isbn_item) as TextView
        ratingBar = v.findViewById<View>(R.id.ratingbar) as RatingBar
        pic = v.findViewById<View>(R.id.pic_item) as ImageView
        category = v.findViewById<View>(R.id.category_list) as TextView
        ic_lu = v.findViewById<View>(R.id.ic_lu) as ImageView
        ic_prete = v.findViewById<View>(R.id.ic_prete) as ImageView
        mAuth = Firebase.auth
        user = mAuth!!.currentUser
        mFireStore = Firebase.firestore
        storageReference = Firebase.storage.reference
        v.setOnLongClickListener(this)
        v.setOnClickListener(this)
    }

    fun updateBooks(
            book: BookModel
    ) {
        title = book.title
        txtTitle!!.text = title
        lastname = book.author
        txtAutorLastname!!.text = lastname
        isbnNumber!!.text = book.isbn
        mrating = book.rating
        ratingBar!!.rating = book.rating
        pic!!.context
        categorie = book.category
        category!!.text = categorie
        info = book.info
        urlImage = book.imageUrl
        isread = book.isread
        isprete = book.islend
      //  Log.e(TAG, "Holder isread : $isread")
      //  Log.e(TAG, "Holder islend : $isprete")

        if(isprete){
            ic_prete!!.setBackgroundResource(R.drawable.ic_prete_valide)
        }else{
            ic_prete!!.setBackgroundResource(R.drawable.ic_prete)
        }

        if(isread){
            ic_lu!!.setBackgroundResource(R.drawable.ic_library_valide)
        }else{
            ic_lu!!.setBackgroundResource(R.drawable.ic_library)
        }

        val desertRef = storageReference!!.child("couvertures/" + user!!.uid + isbnNumber!!.text + ".jpg")
        desertRef?.downloadUrl?.addOnSuccessListener(OnSuccessListener { uri ->
            img_uri = uri
            Glide.with(itemView.context!!).load(uri).apply(RequestOptions.circleCropTransform()).into(pic!!)
        })?.addOnFailureListener { Log.e("BookListHolder", "no image couv : error") }


    }

    override fun onClick(v: View?) {
        val context = itemView.context
        val sharedPref = context?.getSharedPreferences(
                "book", Context.MODE_PRIVATE)
        val ref = mFireStore!!.collection("users")
                .document(user!!.uid).collection("books")
        var idBook = "null"
        ref.get()
                .addOnSuccessListener { book ->

                    for (document in book) {
                        idBook = document.id
                        sharedPref!!.edit().putString("idBook", idBook).commit()
                        sharedPref!!.edit().putString("name", lastname).commit()
                        sharedPref!!.edit().putString("category", categorie).commit()
                        sharedPref!!.edit().putString("title", title).commit()
                        sharedPref!!.edit().putString("info", info).commit()
                        sharedPref!!.edit().putString("urlImage", urlImage).commit()
                        sharedPref!!.edit().putFloat("rating", mrating!!).commit()
                        sharedPref!!.edit().putBoolean("isread",isread).commit()
                        sharedPref!!.edit().putBoolean("islend",isprete).commit()
                     //   Log.e(TAG, "1 - idBook : $idBook")
                     //   Log.e(TAG, "1 - isread : $isread")
                    }
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                }

        val intent = Intent(context, BookDetailActivity::class.java)
        context.startActivity(intent)
    }

    override fun onLongClick(v: View?): Boolean {
        val context = itemView.context
        val ref = mFireStore!!.collection("users")
                .document(user!!.uid).collection("books")
        var idBookk: String? = null
        ref.get()
                .addOnSuccessListener { book ->
                    for (document in book) {
                        idBookk = document.id

                    }
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                }

        val builder = AlertDialog.Builder(context!!)
        builder.setMessage("Voulez-vous supprimer ?").setCancelable(false)
        builder.setPositiveButton("Oui") { dialog, which -> // Delete the file
            ref.document(idBookk!!).delete().addOnCompleteListener {

                Toast.makeText(context, "Ce livre a été supprimé!", Toast.LENGTH_SHORT).show()
            }
                    .addOnFailureListener { e -> Log.e(TAG, "Error deleting document", e) }
        }
        builder.setNegativeButton("Non") { dialog, which ->
            dialog.cancel()
            Toast.makeText(context, "Ce livre ID : $idBookk", Toast.LENGTH_SHORT).show()
        }
        builder.create()
        builder.setTitle("Confirmer")
        builder.show()
        return true
    }

}