package com.helenacorp.android.mybibliotheque

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray
import com.google.firebase.firestore.FirebaseFirestoreException
import com.helenacorp.android.mybibliotheque.model.BookModel
import java.lang.Exception


/**
 * Created by helena on 09/08/2017.
 */
private const val TAG = "BookListAdapter"

class BookListAdapter(options: FirestoreRecyclerOptions<BookModel>)
    : FirestoreRecyclerAdapter<BookModel, BookListHolder>(options), Filterable, LifecycleObserver {

    val originalList: ArrayList<BookModel> = ArrayList()
    private var mOptions: FirestoreRecyclerOptions<BookModel>? = null
    private var mSnapshots: ObservableSnapshotArray<BookModel>? = null
    private var mSnapshotsTotal: ArrayList<BookModel>? = null
    val context: Context? = null

    init {
        mOptions = options
        mSnapshots = options.snapshots
        mSnapshotsTotal = ArrayList<BookModel>(options.snapshots)
        if (options.owner != null) {
            options.owner!!.lifecycle.addObserver(this)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_book, parent, false)
        return BookListHolder(view)
    }

    override fun onBindViewHolder(holder: BookListHolder, position: Int, book: BookModel) {
        holder.updateBooks(book)
    }

    override fun getItem(position: Int): BookModel {
        return mSnapshots!![position]
    }

    override fun getItemId(position: Int): Long {
        super.getItemId(position)
        return mSnapshots!![position].isbn!!.toLong()
    }

    override fun getItemCount(): Int {
        return if (mSnapshots!!.isListening(this)) mSnapshots!!.size else 0
    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e!!)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun startListening() {
        if (!mSnapshots!!.isListening(this)) {
            mSnapshots!!.addChangeEventListener(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun stopListening() {
        mSnapshots!!.removeChangeEventListener(this)
        notifyDataSetChanged()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanup(source: LifecycleOwner) {
        source.lifecycle.removeObserver(this)
    }

    override fun getSnapshots(): ObservableSnapshotArray<BookModel> {
        return mSnapshots!!
    }

    override fun updateOptions(options: FirestoreRecyclerOptions<BookModel>) {
        // Tear down old options
        val wasListening = mSnapshots!!.isListening(this)
        if (mOptions!!.owner != null) {
            mOptions!!.owner!!.lifecycle.removeObserver(this)
        }
        mSnapshots!!.clear()
        stopListening()

        // Set up new options
        mOptions = options
        mSnapshots = options.snapshots
        Log.e(TAG, "mSnapshots updateOptions  : $mSnapshots")
        if (options.owner != null) {
            options.owner!!.lifecycle.addObserver(this)
        }
        if (wasListening) {
            startListening()
        }
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    private val mFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<BookModel> = ArrayList()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(mSnapshotsTotal!!)
            } else {
                val filterPattern = constraint.toString().toLowerCase()
                for (item in mSnapshots!!) {
                    if (item.title!!.toLowerCase().contains(filterPattern) || item.author!!.toLowerCase().contains(filterPattern)
                            || item.category!!.toLowerCase().contains(filterPattern)) {
                        Log.e(TAG, "item : $item")
                        filteredList.add(item)
                    }
                }
                Log.e(TAG, "lsit item : $filteredList")
            }
            val results = FilterResults()
            results.count = filteredList.size
            results.values = filteredList
            Log.e(TAG, "2 - performFiltering  : $filteredList")
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            Log.e(TAG, "1 - publishResult mSnapshots : ${results.values} + count : ${results.count}")
          //  mSnapshots!!.clear()

            try{
                mSnapshotsTotal!!.clear()
                var listBookModel = ArrayList<BookModel>()/**/
                  listBookModel = results.values as ArrayList<BookModel>
              //  @Suppress("UNCHECKED_CAST")
                mSnapshotsTotal!!.addAll(listBookModel)
                //TODO comment afficher cette fichue liste?
                Log.e(TAG, "2 - publishResult mSnapshots : $mSnapshotsTotal")
            }catch(e:Exception){
                Log.e(TAG, "error publisResult: $e")
            }

            notifyDataSetChanged()

            //   adapter.items= filterResults.values as ArrayList<BookModel>
/*            results.values = mSnapshots
            Log.e(TAG, "publishResult mSnapshots : ${results.values} + count : ${results.count}")
            notifyDataSetChanged()*/
        }
    }
    //TODO

}

