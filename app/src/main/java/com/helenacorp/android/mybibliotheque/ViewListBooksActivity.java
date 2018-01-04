package com.helenacorp.android.mybibliotheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewListBooksActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String SAVED_ADAPTER_ITEMS = "SAVED_ADAPTER_ITEMS";
    private final static String SAVED_ADAPTER_KEYS = "SAVED_ADAPTER_KEYS";

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    // private BookListAdapter mBookListAdapter;
    private Button btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Query mQuery;
    private BookListAdapter bookListAdapter;
    private ArrayList<BookModel> mAdapterItems;
    private ArrayList<String> mAdapterKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_books);

        // handleInstanceState(savedInstanceState);
        // setupFirebase();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("books");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        bookListAdapter = new BookListAdapter(mDatabase, mAdapterItems, mAdapterKeys);
        // recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookListAdapter);
        // bookListAdapter.notifyDataSetChanged();
        // recyclerView.invalidate();

        btn = (Button) findViewById(R.id.btn_listbookcreat);
        btn.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("Yeah!");

        // retrieve number of books in listview firebase
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intentV = new Intent();
                intentV.putExtra(AccountActivity.LIST_BOOKS, String.valueOf(dataSnapshot.getChildrenCount()));
                setResult(RESULT_OK, intentV);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_view, menu);

     /*   SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        // searchView.setQuery("", false);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        search(searchView);*/
        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookListAdapter != null) {
            bookListAdapter.destroy();
            bookListAdapter = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bookListAdapter != null) {
            bookListAdapter.destroy();
            bookListAdapter = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // bookListAdapter.destroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view == btn) {
            Intent intent = new Intent(ViewListBooksActivity.this, SubmitBookActivity.class);
            startActivity(intent);
        }
    }

}
