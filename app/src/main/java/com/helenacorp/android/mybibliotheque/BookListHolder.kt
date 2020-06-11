package com.helenacorp.android.mybibliotheque

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private var context: Context? = null
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var img_uri: Uri? = null
    private var mFireStore: FirebaseFirestore? = null
    // private lateinit var idBook:String
    // private var idBook:String? = null

    init {
        txtTitle = v.findViewById<View>(R.id.title_item) as TextView
        txtAutorLastname = v.findViewById<View>(R.id.autorLastName_item) as TextView
        isbnNumber = v.findViewById<View>(R.id.isbn_item) as TextView
        ratingBar = v.findViewById<View>(R.id.ratingbar) as RatingBar
        pic = v.findViewById<View>(R.id.pic_item) as ImageView
        category = v.findViewById<View>(R.id.category_list) as TextView
        resume = v.findViewById(R.id.resum_item)
        keyBook = v.findViewById<View>(R.id.id_item) as TextView
        mAuth = Firebase.auth
        user = mAuth!!.currentUser
        mFireStore = Firebase.firestore
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
        info = book.description
        resume!!.text = info
        urlImage = book.imageUrl
        Log.e(TAG, "Holder title : $txtTitle")
        val storageReference = Firebase.storage.reference
        val desertRef = storageReference.child("couvertures/" + user!!.uid + isbnNumber!!.text + ".jpg")
        desertRef?.downloadUrl?.addOnSuccessListener(OnSuccessListener { uri ->
            img_uri = uri
            Glide.with(itemView.context!!).load(uri).apply(RequestOptions.circleCropTransform()).into(pic!!)
        })?.addOnFailureListener { Log.e("BookListHolder", "error") }

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
                        sharedPref!!.edit().putString("idBook", idBook)
                        sharedPref!!.edit().putString("name", lastname)
                        sharedPref!!.edit().putString("category", categorie)
                        sharedPref!!.edit().putString("title", title)
                        sharedPref!!.edit().putString("info", info)
                        sharedPref!!.edit().putString("urlImage", urlImage)
                        sharedPref!!.edit().putFloat("rating", mrating!!)
                        sharedPref!!.edit().commit()
                        Log.e(TAG, "1 - idBook : $idBook")
                    }
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                }

        val intent = Intent(context, BookDetailActivity::class.java)
        context.startActivity(intent)
        Toast.makeText(context, "onClick : ", Toast.LENGTH_SHORT).show()
    }

    override fun onLongClick(v: View?): Boolean {
        val context = itemView.context
        val ref = mFireStore!!.collection("users")
                .document(user!!.uid).collection("books")
        var idBook: String? = null
        ref.get()
                .addOnSuccessListener { book ->
                    for (document in book) {
                        idBook = document.id

                    }
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                }

        val builder = AlertDialog.Builder(context!!)
        builder.setMessage("Voulez-vous supprimer ?").setCancelable(false)
        builder.setPositiveButton("Oui") { dialog, which -> // Delete the file
            ref.document(idBook!!).delete().addOnCompleteListener {

                Toast.makeText(context, "Ce livre a été supprimé!", Toast.LENGTH_SHORT).show()
            }
                    .addOnFailureListener { e -> Log.e(TAG, "Error deleting document", e) }
        }
        builder.setNegativeButton("Non") { dialog, which ->
            dialog.cancel()
            Toast.makeText(context, "Ce livre ID : $idBook", Toast.LENGTH_SHORT).show()
        }
        builder.create()
        builder.setTitle("Confirmer")
        builder.show()
        return true
    }

}