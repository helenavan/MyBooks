package com.helenacorp.android.mybibliotheque

import android.util.Log
import android.widget.Filter
import com.helenacorp.android.mybibliotheque.model.BookModel

/**
 * Created by helena on 28/08/2017.
 */
private const val TAG = "CustomFilter"

open class CustomFilter(var adapter: BookListAdapter, var filterList: ArrayList<BookModel>? = null) : Filter() {

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
                if (filterList!![i].title!!.toUpperCase().contains(charSequence) || filterList!![i].authors!!.toUpperCase().contains(charSequence)
                        || filterList!![i].category!!.toUpperCase().contains(charSequence)) {
                    filterList!!.add(filterList!![i])
                }
            }
            Log.e(TAG,"1 _publisresult : $filterList")
            results.count = filterableBook.size
            results.values = filterableBook
        } else {
            results.count = filterList!!.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
        Log.e(TAG, "publishResult")
        //   adapter.items= filterResults.values as ArrayList<BookModel>
       // filterResults.values = filterList
        filterResults.values = filterList
        Log.e(TAG,"2 _publisresult : $filterList")
        adapter.notifyDataSetChanged()
    }

    init {
        this.filterList = filterList!!
    }

}