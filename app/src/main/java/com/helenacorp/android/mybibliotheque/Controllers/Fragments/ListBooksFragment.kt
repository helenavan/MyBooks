package com.helenacorp.android.mybibliotheque.Controllers.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.helenacorp.android.mybibliotheque.BookListAdapter
import com.helenacorp.android.mybibliotheque.BookListHolder
import com.helenacorp.android.mybibliotheque.R
import com.helenacorp.android.mybibliotheque.UI.ItemClickSupport
import com.helenacorp.android.mybibliotheque.model.BookModel
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
const val TAG = "ListBooksFragment"

class ListBooksFragment : Fragment(), View.OnClickListener {
    private var recyclerView: RecyclerView? = null
    private var mFireStore: FirebaseFirestore? = null
    private var mAdapter: FirestoreRecyclerAdapter<BookModel, BookListHolder>? = null
    private val mListItems = ArrayList<BookModel>()
    private var firestoreListener: ListenerRegistration? = null
    private val btn: FloatingActionButton? = null
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private var docRef: CollectionReference? = null


    private var searchView: SearchView? = null
    private val floatingActionButton: FloatingActionButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list_books, container, false)

        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser
        mFireStore = Firebase.firestore

        recyclerView = view.findViewById<View>(R.id.recyclerview) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        // retrieve number of books in listview firebase
        docRef = mFireStore!!.collection("users").document(user!!.uid).collection("books")
        loadBookList()
        firestoreListener = docRef!!
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        for (document in snapshot!!) {
                            mListItems.add(document.toObject(BookModel::class.java))
                            Log.e(TAG, "List Books : $mListItems")
                        }
                    }
                    recyclerView!!.setHasFixedSize(true)
                    recyclerView!!.adapter = mAdapter
                    mAdapter!!.notifyDataSetChanged()
                }

        //SEARCH
        searchView = view.findViewById(R.id.mSearch)
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                //FILTER AS YOU TYPE
                //   bookListAdapter!!.filter!!.filter(query)
                return false
            }
        })
        return view
    }

    //ADAPTER
    private fun loadBookList() {
        val option = FirestoreRecyclerOptions.Builder<BookModel>()
                .setQuery(docRef!!, BookModel::class.java)
                .build()
        mAdapter = object : FirestoreRecyclerAdapter<BookModel, BookListHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListHolder {
                return BookListHolder(
                        layoutInflater.inflate(R.layout.item_book, parent, false))
            }

            override fun onBindViewHolder(holder: BookListHolder, position: Int, model: BookModel) {
                holder.updateBooks(mListItems[holder.adapterPosition])
               // Log.e(TAG, "3 - loadBookList mListItems : $mListItems")
            }

            override fun getItemCount() = mListItems.size

            override fun onDataChanged() {
                super.onDataChanged()
            }

            override fun getItemId(position: Int): Long {
                return super.getItemId(position)
            }

        }
        mAdapter!!.notifyDataSetChanged()
        recyclerView!!.adapter = mAdapter
        this.configureOnClickRecyclerView(mListItems)
    }

    private fun configureOnClickRecyclerView(listBooks:ArrayList<BookModel>) {
        var idBook: String? = null
        ItemClickSupport.addTo(recyclerView!!, R.layout.item_book)!!
                .setOnItemClickListener(object : ItemClickSupport.OnItemClickListener {
                    override fun onItemClicked(recyclerView: RecyclerView?, position: Int, v: View?) {
                        val book: BookModel = listBooks[position]
                    }
                })
                .setOnItemLongClickListener(object : ItemClickSupport.OnItemLongClickListener {
                    override fun onItemLongClicked(recyclerView: RecyclerView?, position: Int, v: View?): Boolean {
                        docRef!!.get()
                                .addOnSuccessListener { book ->
                                    for (document in book) {
                                        idBook = document.id
                                        Log.e(TAG, "idBook : $idBook")
                                    }
                                }.addOnFailureListener { exception ->
                                    Log.d(TAG, "Error getting documents: ", exception)
                                }

                        val builder = AlertDialog.Builder(context!!)
                        builder.setMessage("Voulez-vous supprimer ?").setCancelable(false)
                        builder.setPositiveButton("Oui") { dialog, which -> // Delete the file
                            docRef!!.document(idBook!!).delete().addOnSuccessListener {
                                Toast.makeText(activity, "Ce livre a été supprimé!", Toast.LENGTH_SHORT).show()
                            }
                                    .addOnFailureListener { e -> Log.e(TAG, "Error deleting document", e) }
                        }
                        builder.setNegativeButton("Non") { dialog, which -> dialog.cancel() }
                        val dialog = builder.create()
                        dialog.setTitle("Confirmer")
                        dialog.show()
                        return true
                    }

                })
    }

    private fun updateNote(book: BookModel) {
        val intent = Intent(activity, ListBooksFragment::class.java)
        intent.putExtra("UpdateNoteId", book.author)
        intent.putExtra("UpdateNoteTitle", book.title)
        intent.putExtra("UpdateNoteContent", book.category)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    override fun onStart() {
        super.onStart()

        mAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()

        mAdapter!!.stopListening()
    }

    override fun onClick(v: View) {
        Log.e("ListFragment", "onClick :==>")
        val fragment: Fragment = AddBookFragment()
        val fragmentManager = activity!!.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        //  fragmentTransaction.replace(R.id.activity_account_frame, fragment);
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(): ListBooksFragment {
            return ListBooksFragment()
        }
    }
}