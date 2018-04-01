package com.helenacorp.android.mybibliotheque;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

public class ProfileFriendActivity extends AppCompatActivity {

    private TextView friendName,friendStatus, totalBooks, friendCount;
    private ImageView friendPic;
    private Button friendbtn;
    private DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_friend);

        String user_name = getIntent().getStringExtra("user_name");

        friendName = findViewById(R.id.friend_name);
        friendName.setText(user_name);
        friendStatus = findViewById(R.id.friend_status);
        friendPic = findViewById(R.id.friend_pic);
        totalBooks = findViewById(R.id.friend_numberbooks);
    }
}
