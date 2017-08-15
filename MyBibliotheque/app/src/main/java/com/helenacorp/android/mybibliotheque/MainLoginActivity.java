package com.helenacorp.android.mybibliotheque;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

public class MainLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private View viewLayout;
    private Button login;
    private FirebaseAuth mAuth;
    private EditText email_log, password_log;
    private TextView messToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        LayoutInflater layoutInflater = getLayoutInflater();
        viewLayout = layoutInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_layout));
        messToast = (TextView) viewLayout.findViewById(R.id.toast_txt);

        email_log = (EditText) findViewById(R.id.log_email);
        password_log = (EditText) findViewById(R.id.log_password);

        login = (Button) findViewById(R.id.log_btn);
        login.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

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
                            //Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(MainLoginActivity.this, AccountActivity.class);
                            startActivity(intent);


                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Intent intent = new Intent(MainLoginActivity.this, SignupActivity.class);
                            startActivity(intent);
                            messToast.setText("Avez-vous bien un compte?");
                            messageToast();
                        }

                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.log_btn:
                validation();
                break;
        }
    }
}
