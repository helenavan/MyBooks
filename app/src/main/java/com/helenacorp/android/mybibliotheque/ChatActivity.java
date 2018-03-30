package com.helenacorp.android.mybibliotheque;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.helenacorp.android.mybibliotheque.model.MessageModel;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity  {

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView textMessage, nameUser;
        private ImageView userImg,messageImg;

        public MessageViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.message_text);
            nameUser = itemView.findViewById(R.id.message_userName);
            userImg = itemView.findViewById(R.id.message_img);
            messageImg = itemView.findViewById(R.id.message_img_inside);
        }

    }

    private static final String TAG = "ChatActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private static final String MESSAGE_URL = "https://mybibliotheque-46d02.firebaseio.com/message/";
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<MessageModel, MessageViewHolder> mFirebaseAdapter;
    private ProgressBar mProgressBar;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //mUsername = ANONYMOUS;
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //user info
        mUsername = mFirebaseUser.getDisplayName();
        if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.message_RecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        // mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().push();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        SnapshotParser<MessageModel> parser = new SnapshotParser<MessageModel>() {
            @Override
            public MessageModel parseSnapshot(DataSnapshot dataSnapshot) {
                MessageModel friendlyMessage = dataSnapshot.getValue(MessageModel.class);
                if (friendlyMessage != null) {
                    friendlyMessage.setId(dataSnapshot.getKey());
                }
                return friendlyMessage;
            }
        };

        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD);

        FirebaseRecyclerOptions<MessageModel> options =
                new FirebaseRecyclerOptions.Builder<MessageModel>()
                        .setQuery(messagesRef, parser)
                        .build();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<MessageModel, MessageViewHolder>(options) {

            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }


            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder,
                                            int position,
                                            MessageModel friendlyMessage) {

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (friendlyMessage.getMessage() != null) {
                    viewHolder.textMessage.setText(friendlyMessage.getMessage());
                  //  downloadAvatar(viewHolder.userImg);
                    viewHolder.nameUser.setVisibility(TextView.VISIBLE);
                    viewHolder.messageImg.setVisibility(ImageView.GONE);
                } else {
                    String imageUrl = friendlyMessage.getImageUserUrl();
                    if (imageUrl.startsWith("gs://")) {
                        StorageReference storageReference = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(imageUrl);
                        storageReference.getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = task.getResult().toString();
                                            Picasso.with(viewHolder.messageImg.getContext())
                                                    .load(downloadUrl)
                                                    .into(viewHolder.messageImg);
                                        } else {
                                            Log.w(TAG, "Getting download url was not successful.",
                                                    task.getException());
                                        }
                                    }
                                });
                    } else {
                        Picasso.with(viewHolder.messageImg.getContext())
                                .load(friendlyMessage.getImageUserUrl())
                                .into(viewHolder.messageImg);
                    }
                    viewHolder.messageImg.setVisibility(ImageView.VISIBLE);
                    viewHolder.textMessage.setVisibility(TextView.GONE);
                }

                viewHolder.nameUser.setText(friendlyMessage.getUserNameModel());

                if (mPhotoUrl == null) {
                    viewHolder.userImg.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                            R.drawable.ic_user));
                } else {
                  //  downloadAvatar(viewHolder.userImg);
                    Picasso.with(ChatActivity.this)
                            .load(mPhotoUrl)
                            .into(viewHolder.userImg);
                }
//!!!!
                if (friendlyMessage.getMessage() != null) {
                    // write this message to the on-device index
                    FirebaseAppIndex.getInstance().update(getMessageIndexable(friendlyMessage));
                }

                // log a view action on it
                FirebaseUserActions.getInstance().end(getMessageViewAction(friendlyMessage));
            }
        };
        mFirebaseAdapter.notifyDataSetChanged();
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
        mFirebaseAdapter.notifyDataSetChanged();
        // Initialize Firebase Measurement.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();
        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 10L);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        // Fetch remote config.
        fetchConfig();

        mMessageEditText = (EditText) findViewById(R.id.message_EditText);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        //send message
        mSendButton = (Button) findViewById(R.id.message_btn_send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageModel friendlyMessage = new MessageModel(null, mMessageEditText.getText().toString(), mUsername,
                        mPhotoUrl, null);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
                mMessageEditText.setText("");
                mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
            }
        });
    }
    //download and uploadload photoprofil
    private void downloadAvatar(final ImageView image) {
        // Create a storage reference from our app

        StorageReference storageRef = storage.getReference();

        storageRef.child("images/" + mFirebaseUser.getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSuccess(Uri uri) {

                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.WHITE)
                        .borderWidthDp(3)
                        .cornerRadiusDp(20)
                        .oval(true)
                        .build();
                Picasso.with(ChatActivity.this).load(uri).fit().transform(transformation).into(image);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                // Toast.makeText(AccountActivity.this, exception.toString() + "!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Action getMessageViewAction(MessageModel friendlyMessage) {
        return new Action.Builder(com.google.firebase.appindexing.Action.Builder.VIEW_ACTION)
                .setObject(friendlyMessage.getUserNameModel(), MESSAGE_URL.concat(friendlyMessage.getId()))
                .setMetadata(new Action.Metadata.Builder().setUpload(false))
                .build();
    }

    private Indexable getMessageIndexable(MessageModel friendlyMessage) {
        PersonBuilder sender = Indexables.personBuilder()
                .setIsSelf(mUsername.equals(friendlyMessage.getUserNameModel()))
                .setName(friendlyMessage.getUserNameModel())
                .setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder()
                .setName(mUsername)
                .setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/recipient"));

        Indexable messageToIndex = Indexables.messageBuilder()
                .setName(friendlyMessage.getUserNameModel())
                .setUrl(MESSAGE_URL.concat(friendlyMessage.getId()))
                .setSender(sender)
                .setRecipient(recipient)
                .build();

        return messageToIndex;
    }

    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    /*    switch (item.getItemId()) {
            case R.id.invite_menu:
                sendInvitation();
                return true;
            case R.id.crash_menu:
                Log.w("Crashlytics", "Crash button clicked");
                causeCrash();
                return true;
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                mUsername = ANONYMOUS;
                mPhotoUrl = null;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            case R.id.fresh_config_menu:
                fetchConfig();
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }*/
        return true;
    }

    private void causeCrash() {
        throw new NullPointerException("Fake null pointer exception");
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.message_sent))
                .setMessage(getString(R.string.message_sent))
                .setCallToActionText(getString(R.string.message_sent))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available via FirebaseRemoteConfig get<type> calls.
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There has been an error fetching the config
                        Log.w(TAG, "Error fetching config", e);
                        applyRetrievedLengthLimit();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());

                    MessageModel tempMessage = new MessageModel(null, null, mUsername, mPhotoUrl,
                            LOADING_IMAGE_URL);
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(mFirebaseUser.getUid())
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri, key);
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        } else if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Use Firebase Measurement to log that invitation was sent.
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");

                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Use Firebase Measurement to log that invitation was not sent
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, payload);

                // Sending failed or it was canceled, show failure message to the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(ChatActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            MessageModel friendlyMessage =
                                    new MessageModel(null, null, mUsername, mPhotoUrl,
                                            task.getResult().getDownloadUrl()
                                                    .toString());
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key)
                                    .setValue(friendlyMessage);
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    /**
     * Apply retrieved length limit to edit text field. This result may be fresh from the server or it may be from
     * cached values.
     */
    private void applyRetrievedLengthLimit() {
        Long friendly_msg_length = mFirebaseRemoteConfig.getLong("friendly_msg_length");
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_length.intValue())});
        Log.d(TAG, "FML is: " + friendly_msg_length);
    }


}
