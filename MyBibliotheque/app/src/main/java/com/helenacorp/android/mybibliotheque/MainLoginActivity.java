package com.helenacorp.android.mybibliotheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    private Button login;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText email_log, password_log;
    private TextView messToast;

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

        email_log = (EditText) findViewById(R.id.log_email);
        password_log = (EditText) findViewById(R.id.log_password);

        login = (Button) findViewById(R.id.log_btn);
        login.setOnClickListener(this);

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
                    messToast.setText("Veuillez vous reconnecter");
                    messageToast();
                }
                // ...
            }
        };

    }

    public void validation() {
        if (email_log.getText().length() == 0 ||
                password_log.getText().length() == 0) {
            messToast.setText("tsss tout n'est pas là!!!");
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
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(MainLoginActivity.this, AccountActivity.class);
                            startActivity(intent);
                            messToast.setText("Bienvenue!");
                            messageToast();

                        } else {
                            // If sign in fails, display a message to the user.
                            Intent intent = new Intent(MainLoginActivity.this, SignupActivity.class);
                            startActivity(intent);
                            messToast.setText("Avez-vous bien un compte?");
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
        }
    }
/*
    //keep email and password
    @Override
    public void onPause() {
        super.onPause();
        savePreference();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreference();
    }

    private void savePreference() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        // Edit and commit
        UnameValue = email_log.getText().toString();
        PasswordValue = password_log.getText().toString();
        editor.putString(PREF_UNAME, UnameValue);
        editor.putString(PREF_PASSWORD, PasswordValue);
        editor.commit();
    }

    private void loadPreference() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get value
        UnameValue = settings.getString(PREF_UNAME, DefaultUnameValue);
        PasswordValue = settings.getString(PREF_PASSWORD, DefaultPasswordValue);
        email_log.setText(UnameValue);
        password_log.setText(PasswordValue);
        Intent intent = new Intent(MainLoginActivity.this, AccountActivity.class);
        startActivity(intent);
    }*/
}
