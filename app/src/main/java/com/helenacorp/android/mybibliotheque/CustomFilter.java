package com.helenacorp.android.mybibliotheque;

import android.widget.Filter;

import com.helenacorp.android.mybibliotheque.model.BookModel;

import java.util.ArrayList;


/**
 * Created by helena on 28/08/2017.
 */

class CustomFilter extends Filter {

    private FirebaseRecyclerAdapter adapter;
    private ArrayList<BookModel> filterList = new ArrayList<>();

    public CustomFilter(FirebaseRecyclerAdapter adapter, ArrayList<BookModel> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    //filtering ocurs
    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {

        FilterResults results = new FilterResults();
        //CHECK CONSTRAINT VALIDITY
        if (charSequence != null && charSequence.length() > 0) {
            //change to upper
            charSequence = charSequence.toString().toUpperCase();
            //store filtered players
            ArrayList<BookModel> filterableBook = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                //check
                if (filterList.get(i).getTitle().toUpperCase().contains(charSequence) || filterList.get(i).getLastnameAutor().toUpperCase().contains(charSequence)
                        || filterList.get(i).getCategory().toUpperCase().contains(charSequence)) {
                    filterableBook.add(filterList.get(i));
                }
            }
            results.count = filterableBook.size();
            results.values = filterableBook;
        } else {
            results.count = filterList.size();
            results.values = filterList;

        }

        return results;

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.mItems = (ArrayList<BookModel>) filterResults.values;
        // filterList = (ArrayList<BookModel>) filterResults.values;
        adapter.notifyDataSetChanged();
    }

}
