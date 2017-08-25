package com.helenacorp.android.mybibliotheque;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewListBooksActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private DatabaseReference mDatabase;
    private BookListAdapter mBookListAdapter;
    private Button btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private MenuItem searchMenuItem;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_books);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("books");
        mBookListAdapter = new BookListAdapter(mDatabase, R.layout.item_book, this);
        listView = (ListView) this.findViewById(R.id.listView_books);
        listView.setAdapter(mBookListAdapter);

        btn = (Button) findViewById(R.id.btn_listbookcreat);
        btn.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("Yeah!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_view, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        //castz!qqs
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == btn) {
            Intent intent = new Intent(ViewListBooksActivity.this, SubmitBookActivity.class);
            startActivity(intent);
        }
    }
}
