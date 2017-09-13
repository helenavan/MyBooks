package com.helenacorp.android.mybibliotheque;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewListBooksActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private DatabaseReference mDatabase;
    private BookListAdapter mBookListAdapter;
    private Button btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ArrayList<BookModel> booksList;

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

        booksList = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("Yeah!");

        //cont number of books in listview firebase
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
        //count number of books in listview
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshoot : dataSnapshot.getChildren()) {
                    BookModel data = dataSnapshoot.getValue(BookModel.class);
                    booksList.add(data);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_view, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        // searchView.setQuery("", false);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        search(searchView);
        return true;

    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mBookListAdapter.getFilter().filter(newText);
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
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
