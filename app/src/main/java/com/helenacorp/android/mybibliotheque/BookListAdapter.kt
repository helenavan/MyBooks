package com.helenacorp.android.mybibliotheque

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.database.*
import com.helenacorp.android.mybibliotheque.Controllers.Activities.AccountActivity
import com.helenacorp.android.mybibliotheque.model.BookModel
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by helena on 09/08/2017.
 */
class BookListAdapter(options:FirestoreRecyclerOptions<BookModel>)
    : FirestoreRecyclerAdapter<BookModel,BookListHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_book, parent, false)
        return BookListHolder(view)
    }

    override fun onBindViewHolder(holder: BookListHolder, position: Int,book:BookModel) {
        holder.updateBooks(book)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

}

