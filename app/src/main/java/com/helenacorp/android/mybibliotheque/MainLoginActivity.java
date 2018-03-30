package com.helenacorp.android.mybibliotheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainLoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String PREFS_NAME = "preferences";
    private static final String PREF_UNAME = "Username";
    private static final String PREF_PASSWORD = "Password";

    private final String DefaultUnameValue = "";
    private final String DefaultPasswordValue = "";
    private String UnameValue;
    private String PasswordValue;

    private View viewLayout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextInputEditText email_log, password_log;
    private TextInputLayout email_log_parent, password_log_parent;
    private TextView messToast;
    private Button login, auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        //ActionBar actionBar = getSupportActionBar();
        // actionBar.hide();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater layoutInflater = getLayoutInflater();
        viewLayout = layoutInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_layout));
        messToast = (TextView) viewLayout.findViewById(R.id.toast_txt);

        email_log = findViewById(R.id.log_email);
        password_log = findViewById(R.id.log_password);
        email_log_parent = findViewById(R.id.log_email_parent);
        password_log_parent = findViewById(R.id.log_password_parent);

        login = (Button) findViewById(R.id.log_btn);
        login.setOnClickListener(this);
        auth = findViewById(R.id.log_sign_btn);
        auth.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        //to keep connected user
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Intent intent = new Intent(MainLoginActivity.this, AccountActivity.class);
                    startActivity(intent);
                } else {
                    // User is signed out
                   // messToast.setText(R.string.mlog_count);
                   // messageToast();
                }
            }
        };

    }

    public void validation() {
        if (email_log.getText().length() == 0 ||
                password_log.getText().length() == 0) {
            messToast.setText(R.string.mlog_tss);
            messageToast();
        } else {
            sigIn(email_log.getText().toString(), password_log.getText().toString());
        }
    }

    public void messageToast() {
        Toast toast1 = Toast.makeText(MainLoginActivity.this, " ", Toast.LENGTH_SHORT);
        toast1.setGravity(Gravity.CENTER, 0, 0);
        toast1.setView(viewLayout);
        toast1.show();
    }

    private void sigIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(MainLoginActivity.this, AccountActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            messToast.setText("Bonjour " + mAuth.getCurrentUser().getDisplayName());
                            messageToast();

                        } else {
                            // If sign in fails, display a message to the user.
                            //Intent intent = new Intent(MainLoginActivity.this, SignupActivity.class);
                            // startActivity(intent);
                            messToast.setText(R.string.mlog_count);
                            messageToast();
                        }

                    }
                });
    }

    //keep loginuser connected
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.log_btn:
                validation();
                break;
            case R.id.log_sign_btn:
                Intent intent = new Intent(MainLoginActivity.this, SignupActivity.class);
                startActivity(intent);
                break;
        }
    }
}
