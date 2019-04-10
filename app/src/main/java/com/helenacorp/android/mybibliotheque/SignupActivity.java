package com.helenacorp.android.mybibliotheque;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.helenacorp.android.mybibliotheque.Controllers.Activities.AccountActivity;

import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference ref;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextInputEditText authName, authEmail, authPassw;
    private TextInputLayout authNameParent, authEmailParent, authPassParent;
    private Button btnCreatAccount;
    private String displayName, useremail, password, name;
    private View viewLayout;
    private TextView messToast, txtTitle;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        LayoutInflater layoutInflater = getLayoutInflater();
        viewLayout = layoutInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_layout));
        messToast = (TextView) viewLayout.findViewById(R.id.toast_txt);
        txtTitle = findViewById(R.id.txt_signup);
        authName = findViewById(R.id.name_signup);
        authEmail = findViewById(R.id.email_signup);
        authPassw = findViewById(R.id.password_signup);
        authNameParent = findViewById(R.id.name_singup_parent);
        authEmailParent = findViewById(R.id.email_signup_parent);
        authPassParent = findViewById(R.id.password_signup_parent);

        btnCreatAccount = (Button) findViewById(R.id.btn_creatAccount);
        btnCreatAccount.setOnClickListener(this);
        authName.addTextChangedListener(new MyTextWatcher(authName));
        authEmail.addTextChangedListener(new MyTextWatcher(authEmail));
        authPassw.addTextChangedListener(new MyTextWatcher(authPassw));

        mAuth = FirebaseAuth.getInstance();
    }

    public void validation() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }
        loginAccount(displayName = authName.getText().toString(), authEmail.getText().toString(), authPassw.getText().toString());
    }

    private void loginAccount(final String name, String email, String password) {
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

                    }

                });
    }

    public void messageToast() {
        Toast toast1 = Toast.makeText(SignupActivity.this, " ", Toast.LENGTH_SHORT);
        toast1.setGravity(Gravity.CENTER, 0, 0);
        toast1.setView(viewLayout);
        toast1.show();
    }

    private boolean validateName() {
        if (authName.getText().toString().trim().length() < 3) {
            authNameParent.setError(getString(R.string.error_name));
            requestFocus(authName);
            return false;
        } else {
            authNameParent.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (authPassw.getText().toString().trim().length() < 6) {
            authPassParent.setError(getString(R.string.error_password));
            requestFocus(authPassw);
            return false;
        } else {
            authPassParent.setErrorEnabled(false);
        }

        return true;
    }

    public boolean validateEmail() {
        String email = authEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            authEmailParent.setError(getString(R.string.error_email));
            requestFocus(authEmail);
            return false;
        } else {
            authEmailParent.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.name_signup:
                    validateName();
                    break;
                case R.id.email_signup:
                    validateEmail();
                    break;
                case R.id.password_signup:
                    validatePassword();
                    break;
            }
        }
    }
}
