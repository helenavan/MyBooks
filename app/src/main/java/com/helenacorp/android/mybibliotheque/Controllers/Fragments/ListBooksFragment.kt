package com.helenacorp.android.mybibliotheque.Controllers.Fragments

import android.annotation.SuppressLint
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
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.helenacorp.android.mybibliotheque.BookListHolder
import com.helenacorp.android.mybibliotheque.R
import com.helenacorp.android.mybibliotheque.UI.ItemClickSupport
import com.helenacorp.android.mybibliotheque.model.BookModel


/**
 * A simple [Fragment] subclass.
 */
private const val TAG = "ListBooksFragment"

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

        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        // retrieve number of books in listview firebase
        docRef = mFireStore!!.collection("users").document(user!!.uid).collection("books")
        //loadBookList()
        firestoreListener = docRef!!
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        for (document in snapshot!!) {
                            val book = document.toObject(BookModel::class.java)
                            mListItems.add(book)
                            Log.e(TAG, "List Books : $mListItems")
                        }
                    }
                    for (dc in snapshot!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> Log.d(TAG, "New city: " + dc.document.data)
                            DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified city: " + dc.document.data)
                            DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed city: " + dc.document.data)
                        }
                    }
                    //recyclerView!!.setHasFixedSize(true)
                 //   mAdapter!!.notifyDataSetChanged()
                 //   recyclerView!!.adapter = mAdapter

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
                configureOnClickRecyclerView(mListItems)
               // Log.e(TAG, "3 - loadBookList mListItems : $mListItems")
            }

            override fun getItemCount() = mListItems.size

/*            override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {
                    when (type) {
                        ChangeEventType.ADDED -> notifyItemInserted(newIndex)
                        ChangeEventType.CHANGED -> //notifyItemChanged(newIndex)
                            Log.e(TAG, "DocumentSnapshot CHANGED: " + snapshot.id)
                        ChangeEventType.REMOVED -> notifyItemRemoved(newIndex)
                        ChangeEventType.MOVED->// notifyItemMoved(oldIndex, newIndex)
                            Log.e(TAG, "DocumentSnapshot MOVED: " + snapshot.id)
                    }
            }*/

            override fun onDataChanged() {
                super.onDataChanged()
            }

            fun onCalled(error: DatabaseError){
                Log.w(TAG, error.toException())
            }

            override fun getItemId(position: Int): Long {
                return super.getItemId(position)
            }

        }
      //  mAdapter!!.notifyDataSetChanged()
     //   recyclerView!!.adapter = mAdapter
       // this.configureOnClickRecyclerView(mListItems)
    }

    private fun configureOnClickRecyclerView(listBooks:ArrayList<BookModel>) {
        var idBook: String? = null
        ItemClickSupport.addTo(recyclerView!!, R.layout.item_book)!!
                .setOnItemClickListener(object : ItemClickSupport.OnItemClickListener {
                    override fun onItemClicked(recyclerView: RecyclerView?, position: Int, v: View?) {
                        val book: BookModel = listBooks[position]
                        Toast.makeText(activity, "book : ${book.title}",Toast.LENGTH_LONG).show()
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
                                    Log.e(TAG, "Error getting documents: ", exception)
                                }

                        val builder = AlertDialog.Builder(context!!)
                        builder.setMessage("Voulez-vous supprimer ?").setCancelable(false)
                        builder.setPositiveButton("Oui") { dialog, which -> // Delete the file
                            docRef!!.document(idBook!!).delete().addOnCompleteListener {
/*                                mListItems.removeAt(position)
                                mAdapter!!.notifyItemRemoved(position)
                                mAdapter!!.notifyItemRangeChanged(position, mListItems.size)*/
                                Toast.makeText(activity, "Ce livre a été supprimé!", Toast.LENGTH_SHORT).show()
                            }
                                    .addOnFailureListener { e -> Log.e(TAG, "Error deleting document", e) }
                        }
                        builder.setNegativeButton("Non") { dialog, which -> dialog.cancel()
                            Toast.makeText(activity, "Ce livre ID : $idBook", Toast.LENGTH_SHORT).show()}
                        val dialog = builder.create()
                        dialog.setTitle("Confirmer")
                        dialog.show()
                        return true
                    }

                })
    }

    private fun updateBookData(){
        docRef!!
                .addSnapshotListener(com.google.firebase.firestore.EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "listen:error", e)
                        return@EventListener
                    }
                    for (doc in snapshots!!) {
                        val bookModel = doc.toObject(BookModel::class.java)

                        mListItems.add(bookModel)
                    }
                    // instead of simply using the entire query snapshot
                    // see the actual changes to query results between query snapshots (added, removed, and modified)
                    for (dc in snapshots.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED ->

                                Log.d(TAG, "New city: " + dc.document.data)
                            DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified city: " + dc.document.data)
                            DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed city: " + dc.document.data)
                        }
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

      //  mAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()

       // mAdapter!!.stopListening()
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
        private const val ARG_SECTION_NUMBER = "section_number"
        @JvmStatic
        fun newInstance(sectionNumber:Int): ListBooksFragment {
            return ListBooksFragment().apply{
                arguments = Bundle().apply{
                    putInt(ARG_SECTION_NUMBER,sectionNumber)
                }
            }
        }
    }
}