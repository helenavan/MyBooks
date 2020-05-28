package com.helenacorp.android.mybibliotheque

import android.widget.Filter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.helenacorp.android.mybibliotheque.model.BookModel

import kotlin.collections.ArrayList

/**
 * Created by helena on 28/08/2017.
 */
class CustomFilter(var adapter: FirebaseRecyclerAdapter<*, *>, var filterList: ArrayList<BookModel>? = null) : Filter() {

    //filtering ocurs
    override fun performFiltering(charSequence: CharSequence): FilterResults {
        var charSequenceb: CharSequence? = charSequence
        val results = FilterResults()
        //CHECK CONSTRAINT VALIDITY
        if (charSequenceb != null && charSequence.length > 0) {
            //change to upper
            charSequenceb = charSequence.toString().toUpperCase()
            //store filtered players
            val filterableBook = ArrayList<BookModel>()
            for (i in filterList!!.indices) {
                //check
                if (filterList!![i].title!!.toUpperCase().contains(charSequence) || filterList!![i].author!!.toUpperCase().contains(charSequence)
                        || filterList!![i].category!!.toUpperCase().contains(charSequence)) {
                    filterableBook.add(filterList!![i])
                }
            }
            results.count = filterableBook.size
            results.values = filterableBook
        } else {
            results.count = filterList!!.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

    //    adapter.items= filterResults.values as ArrayList<BookModel>
        // filterList = (ArrayList<BookModel>) filterResults.values;
        adapter.notifyDataSetChanged()
    }

    init {
        this.filterList = filterList!!
    }
}