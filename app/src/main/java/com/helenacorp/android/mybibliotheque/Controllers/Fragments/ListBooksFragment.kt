package com.helenacorp.android.mybibliotheque.Controllers.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.helenacorp.android.mybibliotheque.BookListAdapter
import com.helenacorp.android.mybibliotheque.R
import com.helenacorp.android.mybibliotheque.model.BookModel

private const val TAG = "ListBooksFragment"

class ListBooksFragment : Fragment(), View.OnClickListener {
    private var recyclerView: RecyclerView? = null
    private var mFireStore: FirebaseFirestore? = null
    private var mAdapter: BookListAdapter? = null
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private lateinit var query: Query

    private var searchView: SearchView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list_books, container, false)

        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser
        // mFireStore = Firebase.firestore
        mFireStore = FirebaseFirestore.getInstance()


        // retrieve number of books in listview firebase
        query = mFireStore!!.collection("users").document(user!!.uid).collection("books")
        val options = FirestoreRecyclerOptions.Builder<BookModel>()
                .setQuery(query, BookModel::class.java)
                .build()
        mAdapter = BookListAdapter(options)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        recyclerView!!.adapter = mAdapter
        mAdapter!!.notifyDataSetChanged()

        //SEARCH
        searchView = view.findViewById(R.id.mSearch)
        searchView!!.queryHint = "recherche par titre"
        searchView!!.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
               // mAdapter!!.filter.filter(newText)
                val filteredListQuery =
                    query.whereGreaterThanOrEqualTo("title", newText.toLowerCase())
                            .whereLessThanOrEqualTo("title", newText.toLowerCase() + "\uf8ff")
                val originalListOptions = FirestoreRecyclerOptions.Builder<BookModel>()
                        .setQuery(query, BookModel::class.java)
                        .setLifecycleOwner(this@ListBooksFragment)
                        .build()
                val filteredListOptions = FirestoreRecyclerOptions.Builder<BookModel>()
                        .setQuery(filteredListQuery, BookModel::class.java)
                        .setLifecycleOwner(this@ListBooksFragment)
                        .build()
                if (newText == "")
                    mAdapter!!.updateOptions(originalListOptions)
                else
                    mAdapter!!.updateOptions(filteredListOptions)
                return false
            }
        })

        return view
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

        //  firestoreListener!!.remove()
    }

    override fun onStart() {
        super.onStart()
        mAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (mAdapter != null)
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
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): ListBooksFragment {
            return ListBooksFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}