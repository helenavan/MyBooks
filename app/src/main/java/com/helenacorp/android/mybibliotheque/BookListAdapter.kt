package com.helenacorp.android.mybibliotheque

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.helenacorp.android.mybibliotheque.model.BookModel

/**
 * Created by helena on 09/08/2017.
 */
class BookListAdapter(options: FirestoreRecyclerOptions<BookModel>)
    : FirestoreRecyclerAdapter<BookModel, BookListHolder>(options), Filterable {

    var list: ArrayList<BookModel> = ArrayList()
    val originalList: ArrayList<BookModel> = ArrayList<BookModel>()
    val context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_book, parent, false)
        return BookListHolder(view)
    }

    override fun onBindViewHolder(holder: BookListHolder, position: Int, book: BookModel) {
        holder.updateBooks(book)
    }

    override fun getItem(position: Int): BookModel {
        return super.getItem(position)
    }

    override fun getItemId(position: Int): Long {
        super.getItemId(position)
        return list[position].isbn!!.toLong()
    }

    //TODO
    override fun getFilter(): Filter {
        return CustomFilter(this, list!!)
    }


}

