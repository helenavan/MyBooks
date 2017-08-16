package com.helenacorp.android.mybibliotheque;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SubmitBookActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText titleName, firstName, lastName;
    private Button btnClean, btnAdd, btnPic;
    private RatingBar ratingBar;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_book);

        titleName = (EditText) findViewById(R.id.title_submit);
        firstName = (EditText) findViewById(R.id.nameAutor_submit);
        lastName = (EditText) findViewById(R.id.autorLastName_submit);
        btnClean = (Button) findViewById(R.id.btn_clean_submit);
        btnAdd = (Button) findViewById(R.id.btn_add_submit);
        btnPic = (Button) findViewById(R.id.btn_photoBook_submit);
        ratingBar = (RatingBar) findViewById(R.id.submit_rating);

        btnAdd.setOnClickListener(this);
        btnClean.setOnClickListener(this);
        btnPic.setOnClickListener(this);
    }

    public void validation() {
        if (titleName.getText().length() == 0 || firstName.length() == 0) {
            Context context = getApplicationContext();
            CharSequence text = "Ha! N'avez-vous pas oublié quelque chose?";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            sendBook(btnAdd);
        }
    }

    public void sendBook(View view) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String userName = user.getDisplayName();
        //write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("book");
        BookModel bookModel = new BookModel(titleName.getText().toString(), null, null, firstName.getText().toString(),
                lastName.getText().toString(), userName, 0, ratingBar.getNumStars());
        ref.push().setValue(bookModel);
        finish();
    }

    public void clearEditText(EditText editText) {
        editText.setText("");
    }

    @Override
    public void onClick(View view) {
        if (view == btnAdd) {
            validation();
        }
        if (view == btnClean) {
            clearEditText(titleName);
            clearEditText(firstName);
            clearEditText(lastName);
        }
        if (view == btnPic) {

        }
    }

}
