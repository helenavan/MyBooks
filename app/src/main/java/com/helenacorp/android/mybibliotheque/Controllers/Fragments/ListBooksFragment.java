package com.helenacorp.android.mybibliotheque.Controllers.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helenacorp.android.mybibliotheque.BookListAdapter;
import com.helenacorp.android.mybibliotheque.Controllers.Activities.AccountActivity;
import com.helenacorp.android.mybibliotheque.OnBookItemClick;
import com.helenacorp.android.mybibliotheque.R;
import com.helenacorp.android.mybibliotheque.model.BookModel;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListBooksFragment extends Fragment implements View.OnClickListener{

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FloatingActionButton btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private BookListAdapter bookListAdapter;
    private ArrayList<BookModel> mAdapterItems = new ArrayList<>();
    private ArrayList<String> mAdapterKeys = new ArrayList<>();
    private OnBookItemClick mOnBook;
    private SearchView searchView;
    private FloatingActionButton floatingActionButton;

    public static ListBooksFragment newInstance() {
        return (new ListBooksFragment());
    }

    //2 - Declare callback
    private OnButtonClickedListener mCallback;


    // 1 - Declare our interface that will be implemented by any container activity
    public interface OnButtonClickedListener {
        public void onButtonClicked(View view);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_books, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("books");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        bookListAdapter = new BookListAdapter(mDatabase, mAdapterItems, mAdapterKeys, mOnBook);
        recyclerView.setAdapter(bookListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookListAdapter.notifyDataSetChanged();

        floatingActionButton = view.findViewById(R.id.btn_float);
        floatingActionButton.setOnClickListener(this);

        btn = view.findViewById(R.id.btn_float);
        btn.setOnClickListener(this);
        bookListAdapter.notifyDataSetChanged();

        // retrieve number of books in listview firebase
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intentV = new Intent();
                intentV.putExtra(AccountActivity.LIST_BOOKS, String.valueOf(dataSnapshot.getChildrenCount()));
                getActivity().setResult(RESULT_OK, intentV);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        bookListAdapter.notifyDataSetChanged();
        searchView = view.findViewById(R.id.mSearch);


        //SEARCH
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //FILTER AS YOU TYPE
                bookListAdapter.getFilter().filter(query);
                return false;
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        Log.e("ListFragment","onClick :==>");
        Fragment fragment = new AddBookFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_account_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
