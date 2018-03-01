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
    private ArrayList<BookModel> filteredList = new ArrayList<>();

    public CustomFilter(FirebaseRecyclerAdapter adapter, ArrayList<BookModel> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    //filtering ocurs
    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        filteredList.clear();
        FilterResults results = new FilterResults();
        //CHECK CONSTRAINT VALIDITY
        if(charSequence.length() == 0){
            filteredList.addAll(filterList);
        } else {

            for (BookModel bookM : filterList) {
                if (bookM.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())){
                    filteredList.add(bookM);
                }
            }
            // //  for( BookModel mBook : book)
            results.count = filteredList.size();
            results.values = filteredList;
        }
        return results;

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        filterList = (ArrayList<BookModel>) filterResults.values;
        adapter.notifyDataSetChanged();
    }

}
