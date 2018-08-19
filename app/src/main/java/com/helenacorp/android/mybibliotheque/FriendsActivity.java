package com.helenacorp.android.mybibliotheque;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.helenacorp.android.mybibliotheque.model.Friends;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;


public class FriendsActivity extends AppCompatActivity {

    private RecyclerView mFriendList;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private DatabaseReference mFriendsDatabase, mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;
    private String list_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        Toolbar tlb = findViewById(R.id.toolbar_friends_list);
        setSupportActionBar(tlb);
        getSupportActionBar().setTitle("Mes potes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Inflate the layout for this fragment
        mFriendList = findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));



        Query query = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        query.keepSynced(true);
        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends,FriendsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int i, @NonNull Friends friends) {

                friendsViewHolder.setDate(friends.getDate());

                list_user_id = getRef(i).getKey();

                final String user_name = friends.getDate();


                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("username").getValue().toString();
                        String userPic = dataSnapshot.child("picChatUser").getValue().toString();

                        if(dataSnapshot.hasChild("online")){
                            String userOnline =  dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setOnline(userOnline);

                        }
                    //    String userOnline = dataSnapshot.child("online").getValue().toString();

                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setPic(userPic, getApplicationContext());


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_friends_list, parent, false);
                return new FriendsViewHolder(view);

            }
        };
        mFriendList.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    public void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();

    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        private ImageView userPic;

        private FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            userPic = mView.findViewById(R.id.item_list_picFriends);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence option[] = new CharSequence[]{"Open Profile", "send message"};
                    final Query query = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
                    AlertDialog.Builder builder= new AlertDialog.Builder(mView.getContext());
                    builder.setTitle("Select Option");
                    builder.setItems(option, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which == 0){
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Intent intent = new Intent(getApplicationContext(), ProfileFriendActivity.class );
                                        intent.putExtra("user_id", list_user_id);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            if(which == 1){
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    });
                    builder.show();
                }
            });
        }

            public void setDate(String date) {
                TextView userNameView = mView.findViewById(R.id.item_friends_date);
                userNameView.setText(date);
            }

            public void setName(String name) {

                TextView userName = (TextView) mView.findViewById(R.id.item_name_list_friends);
                userName.setText(name);

            }

            public void setPic(String uri, Context context) {

                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(R.color.vertD)
                        .borderWidthDp(3)
                        .cornerRadiusDp(30)
                        .oval(false)
                        .build();

                Picasso.with(context)
                        .load(uri)
                        .placeholder(R.drawable.ic_user)
                        .fit()
                        .transform(transformation)
                        .into(userPic);
            }

            public void setOnline(String online_status) {
                ImageView userOnlineView = mView.findViewById(R.id.item_ic_status_friends);
                if (online_status.equals("true")) {
                    userOnlineView.setVisibility(View.VISIBLE);
                } else {
                    userOnlineView.setVisibility(View.INVISIBLE);
                }

            }

        }

    }
