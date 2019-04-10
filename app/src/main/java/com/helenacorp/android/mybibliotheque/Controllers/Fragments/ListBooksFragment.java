package com.helenacorp.android.mybibliotheque.Controllers.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.helenacorp.android.mybibliotheque.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListBooksFragment extends Fragment {


    public ListBooksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_books, container, false);
    }

}
