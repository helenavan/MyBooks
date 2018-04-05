package com.helenacorp.android.mybibliotheque;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileFriendActivity extends AppCompatActivity {

    private TextView friendName, friendStatus, totalBooks, friendCount;
    private ImageView friendPic;
    private Button friendbtn, notfriendbtn;
    private DatabaseReference mData, mData_request, mData_friend, mNotificationDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ProgressDialog mProgress;
    private String current_state ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_friend);

        final String user_id = getIntent().getStringExtra("user_id");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mData = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        mData_request = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mData_friend  = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        friendName = findViewById(R.id.friend_name);
        friendStatus = findViewById(R.id.friend_status);
        friendPic = findViewById(R.id.friend_pic);
        totalBooks = findViewById(R.id.friend_numberbooks);
        friendbtn = findViewById(R.id.friend_button);
        notfriendbtn = findViewById(R.id.friend_btn_decline);

        Toolbar toolbar = findViewById(R.id.friend_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        current_state = "not_friends";

        mProgress = new ProgressDialog(ProfileFriendActivity.this);
        mProgress.setMessage(getString(R.string.charge_profil));
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

     /*   final Bundle bundle = getIntent().getExtras();
        friendName.setText(bundle.getString("username"));
        friendStatus.setText(bundle.getString("status"));
        final String url = bundle.getString("picChatUser");

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.WHITE)
                .borderWidthDp(3)
                .cornerRadiusDp(20)
                .oval(true)
                .build();

        Picasso.with(ProfileFriendActivity.this).load(url).placeholder(R.drawable.ic_user).fit().transform(transformation).into(friendPic);*/

      //  mProgress.dismiss();

        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("username").getValue().toString();
                String display_status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("picChatUser").getValue().toString();

                friendName.setText(display_name);
                friendStatus.setText(display_status);

                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.WHITE)
                        .borderWidthDp(3)
                        .cornerRadiusDp(20)
                        .oval(true)
                        .build();

                Picasso.with(ProfileFriendActivity.this).load(image).placeholder(R.drawable.ic_user).fit().transform(transformation).into(friendPic);

                //----FRIENDS LIST / REQUEST FEATURE
                mData_request.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("received")){
                                current_state = "req_received";
                                friendbtn.setText("Accept friend request");

                                notfriendbtn.setVisibility(View.VISIBLE);
                                notfriendbtn.setEnabled(true);

                            }else if(req_type.equals("sent")){
                                current_state ="req_sent";
                                friendbtn.setText(("Cancel Freind Request"));

                                notfriendbtn.setVisibility(View.INVISIBLE);
                                notfriendbtn.setEnabled(false);
                            }
                            mProgress.dismiss();

                        }else {
                            mData_friend.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id)){
                                        current_state = "friends";
                                        friendbtn.setText("Unfriend this person");

                                        notfriendbtn.setVisibility(View.INVISIBLE);
                                        notfriendbtn.setEnabled(false);
                                    }
                                    mProgress.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mProgress.dismiss();
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        friendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friendbtn.setEnabled(false);
                //---- NOT FRIENDS STATE
                if(current_state.equals("not_friends")){
                    mData_request.child(user.getUid()).child(user_id).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        mData_request.child(user_id).child(user.getUid()).child("request_type").setValue("received")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        HashMap<String, String> notificationData = new HashMap<>();
                                                        notificationData.put("from", user.getUid());
                                                        notificationData.put("type", "request");

                                                        mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                current_state = "req_sent";
                                                                friendbtn.setText("Cancel friend request");

                                                                notfriendbtn.setVisibility(View.INVISIBLE);
                                                                notfriendbtn.setEnabled(false);
                                                            }
                                                        });

                                                       // Toast.makeText(ProfileFriendActivity.this,"request sent successfull", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }else {
                                        Toast.makeText(ProfileFriendActivity.this,"failling sending request", Toast.LENGTH_LONG).show();
                                    }
                                    friendbtn.setEnabled(true);
                                }
                            });
                }
                //---CANCEL REQUEST
                if(current_state.equals("req_sent")){

                    mData_request.child(user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mData_request.child(user_id).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendbtn.setEnabled(true);
                                    current_state = "not_friends";
                                    friendbtn.setText("Send friend request");

                                    notfriendbtn.setVisibility(View.INVISIBLE);
                                    notfriendbtn.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                //----REQ RECEIVED STATE
                if(current_state.equals("req_received")){
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mData_friend.child(user.getUid()).child(user_id).setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mData_friend.child(user_id).child(user.getUid()).setValue(currentDate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mData_request.child(user.getUid()).child(user_id).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mData_request.child(user_id).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    friendbtn.setEnabled(true);
                                                    current_state = "friends";
                                                    friendbtn.setText("Unfriend this person");

                                                    notfriendbtn.setVisibility(View.INVISIBLE);
                                                    notfriendbtn.setEnabled(false);
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        }
                    });
                }
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(ProfileFriendActivity.this, AccountActivity.class);
                startActivity(intent);
                break;
            case R.id.action_list:
                Intent i = new Intent(ProfileFriendActivity.this, ViewListBooksActivity.class);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
