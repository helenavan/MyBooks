package com.helenacorp.android.mybibliotheque

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
class BookListHolder(v: View) : RecyclerView.ViewHolder(v),View.OnClickListener,View.OnLongClickListener {

    var txtTitle: TextView? = null
    private var txtAutorLastname: TextView? = null
    private var isbnNumber: TextView? = null
    private var resume: TextView? = null
    private var keyBook: TextView? = null
    private var category: TextView? = null
    private var ratingBar: RatingBar? = null
    private var pic: ImageView? = null
    private var context: Context? = null
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var img_uri: Uri? = null
    private val idBook: String? = null
    private var listBooks: ArrayList<BookModel>? = null
    private var docRef: CollectionReference? = null
    private var mFireStore: FirebaseFirestore? = null

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
        v.setOnLongClickListener(this)
        v.setOnClickListener(this)
    }

    fun updateBooks(
            book: BookModel
    ) {
        txtTitle!!.text = book.title
        txtAutorLastname!!.text = book.author
        isbnNumber!!.text = book.isbn
        ratingBar!!.rating = book.rating
        pic!!.context
        category!!.text = book.category
        resume!!.text = book.description
        Log.e(TAG,"Holder title : $txtTitle")
        val storageReference = Firebase.storage.reference
        val desertRef = storageReference.child("couvertures/" + user!!.uid + isbnNumber!!.text+ ".jpg")
        desertRef.downloadUrl.addOnSuccessListener(OnSuccessListener { uri ->
            img_uri = uri
            Glide.with(itemView.context!!).load(uri).apply(RequestOptions.circleCropTransform()).into(pic!!)
        }).addOnFailureListener { Log.e("BookListHolder", "e") }
    }

    override fun onClick(v: View?) {
        val context = itemView.context
        Toast.makeText(context, "onClick : ", Toast.LENGTH_SHORT).show()
    }

    override fun onLongClick(v: View?): Boolean {
        val context = itemView.context
        mFireStore = Firebase.firestore
        val ref = mFireStore!!.collection("users")
                .document(user!!.uid).collection("books")
        var idBook: String? = null
                ref.get()
                .addOnSuccessListener { book ->
                    for (document in book) {
                        idBook = document.id
                        Log.e(TAG, "idBook : $idBook")
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