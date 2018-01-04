package com.helenacorp.android.mybibliotheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference ref;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText authName, authEmail, authPassw;
    private Button btnCreatAccount;
    private String displayName;
    private ProgressBar mProgressBar;
    private View viewLayout;
    private TextView messToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        LayoutInflater layoutInflater = getLayoutInflater();
        viewLayout = layoutInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_layout));
        messToast = (TextView) viewLayout.findViewById(R.id.toast_txt);

        authName = (EditText) findViewById(R.id.name_signup);
        authEmail = (EditText) findViewById(R.id.email_signup);
        authPassw = (EditText) findViewById(R.id.password_signup);
        btnCreatAccount = (Button) findViewById(R.id.btn_creatAccount);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
        btnCreatAccount.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    public void validation() {
        if (authName.getText().length() == 0 ||
                authEmail.getText().length() == 0 || authPassw.getText().length() == 0) {
            messToast.setText(R.string.mlog_tss);
            messageToast();
        } else {
            loginAccount(displayName = authName.getText().toString(), authEmail.getText().toString(), authPassw.getText().toString());
        }
    }
    private void loginAccount(final String name, String email, String password) {
        //showProgressDialog();
        mProgressBar.setVisibility(View.VISIBLE);
        // START create_user_with_email
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            messToast.setText(R.string.mlog_bvn);
                            messageToast();
                            user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();
                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    messToast.setText(R.string.mlog_bvn);
                                    messageToast();
                                    Intent intent = new Intent(SignupActivity.this, AccountActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            });


                        } else {
                            messToast.setText(R.string.mlog_signup);
                            messageToast();
                            // If sign in fails, display a message to the user.
                        }

                        //hideProgressDialog();
                    }

                });
    }

    public void messageToast() {
        Toast toast1 = Toast.makeText(SignupActivity.this, " ", Toast.LENGTH_SHORT);
        toast1.setGravity(Gravity.CENTER, 0, 0);
        toast1.setView(viewLayout);
        toast1.show();
    }

/*
    @Override
    public void onStop(){
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }*/

    @Override
    public void onClick(View view) {
        if (view == btnCreatAccount) {
            validation();
        }
    }
}
