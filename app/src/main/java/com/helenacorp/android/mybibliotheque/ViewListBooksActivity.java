package com.helenacorp.android.mybibliotheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewListBooksActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FloatingActionButton btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private BookListAdapter bookListAdapter;
    private ArrayList<BookModel> mAdapterItems = new ArrayList<>();
    private ArrayList<String> mAdapterKeys = new ArrayList<>();

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
        recyclerView.setHasFixedSize(true);
        bookListAdapter = new BookListAdapter(mDatabase, mAdapterItems, mAdapterKeys);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewListBooksActivity.this));
        bookListAdapter.notifyDataSetChanged();
        //recyclerView.invalidate();

        btn = findViewById(R.id.btn_listbookcreat);
        btn.setOnClickListener(this);
        bookListAdapter.notifyDataSetChanged();

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
        bookListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_view, menu);
        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
