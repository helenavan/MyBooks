package com.helenacorp.android.mybibliotheque;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.helenacorp.android.mybibliotheque.model.BookModel;
import com.helenacorp.android.mybibliotheque.model.ChatUser;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Created by helena on 21/03/2018.
 */

public class UserList extends AppCompatActivity {
    /** Users database reference */
    private DatabaseReference database;
    private RecyclerView mRecyclerview;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private ImageView imagePic;
    /** The user. */
    public static ChatUser user;

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        imagePic = findViewById(R.id.item_list_picUser);
        // Get reference to the Firebase database
        database  = FirebaseDatabase.getInstance().getReference().child("users");

        mRecyclerview = findViewById(R.id.list_users);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Toolbar tlb = findViewById(R.id.toolbar_listUserChat);
        setSupportActionBar(tlb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Query query = FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseRecyclerOptions<ChatUser> options =
                new FirebaseRecyclerOptions.Builder<ChatUser>()
                        .setQuery(query, ChatUser.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatUser,UsersViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, int i, @NonNull ChatUser chatUser) {
                usersViewHolder.setName(chatUser.getUsername());
                usersViewHolder.setStatus(chatUser.getStatus());
                usersViewHolder.setPic(chatUser.getPicChatUser());

                final String user_id = getRef(i).getKey();

                final String user_name = chatUser.getUsername();
                final String user_status = chatUser.getStatus();
                final String user_pic = chatUser.getPicChatUser();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(UserList.this, ProfileFriendActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        profileIntent.putExtra("username",user_name);
                        profileIntent.putExtra("status", user_status);
                        profileIntent.putExtra("picChatUser", user_pic);
                        startActivity(profileIntent);
                    }
                });

            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_user_list, parent, false);
                return new UsersViewHolder(view);

            }
        };

        mRecyclerview.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onStart(){
        super.onStart();

        firebaseRecyclerAdapter.startListening();

    }

    @Override
    protected void onStop(){
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private Context context;
        private ImageView userPic;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            userPic = mView.findViewById(R.id.item_list_picUser);

        }

        public void setName(String name){

            TextView userName = (TextView) mView.findViewById(R.id.item_name_list);
            userName.setText(name);

        }

        public void setStatus(String status){
            TextView userStatus = mView.findViewById(R.id.item_status_list);
            userStatus.setText(status);
        }

        public void setPic(String uri){
            context = userPic.getContext();
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

    }

}