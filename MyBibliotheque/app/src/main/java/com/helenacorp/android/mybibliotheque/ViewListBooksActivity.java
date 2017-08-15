package com.helenacorp.android.mybibliotheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewListBooksActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private DatabaseReference mDatabase;
    private BookListAdapter mBookListAdapter;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_books);

        mDatabase = FirebaseDatabase.getInstance().getReference("book");
        mBookListAdapter = new BookListAdapter(mDatabase, R.layout.item_book, this);
        listView = (ListView) this.findViewById(R.id.listView_books);
        listView.setAdapter(mBookListAdapter);

        btn = (Button) findViewById(R.id.btn_listbookcreat);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btn) {
            Intent intent = new Intent(ViewListBooksActivity.this, SubmitBookActivity.class);
            startActivity(intent);
        }
    }
}
