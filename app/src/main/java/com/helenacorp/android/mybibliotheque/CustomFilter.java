package com.helenacorp.android.mybibliotheque;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by helena on 28/08/2017.
 */

class CustomFilter extends Filter {

    private BookListAdapter adapter;
    private List<BookModel> filterList = new ArrayList<>();
    private List<BookModel> filteredList = new ArrayList<>();

    public CustomFilter(BookListAdapter adapter, List<BookModel> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    //filtering ocurs
    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        //CHECK CONSTRAINT VALIDITY
        if (charSequence != null && charSequence.length() > 0) {
            ArrayList<BookModel> tempList = new ArrayList<BookModel>();

            for (BookModel bookM : filterList) {
                if (bookM.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase()))
                    ;
                tempList.add(bookM);
            }
            // //  for( BookModel mBook : book)
            results.count = tempList.size();
            results.values = tempList;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        filteredList = (List<BookModel>) filterResults.values;
        adapter.notifyDataSetChanged();
    }

}
