package com.helenacorp.android.mybibliotheque

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.helenacorp.android.mybibliotheque.Controllers.Activities.AccountActivity

private const val TAG = "MainLoginActivity"

class MainLoginActivity : AppCompatActivity(), View.OnClickListener {

    private var viewLayout: View? = null
    private var mAuth: FirebaseAuth? = null
    private lateinit var mAuthListener: AuthStateListener
    private lateinit var  user:FirebaseUser
    private lateinit var auth: FirebaseAuth
    private var email_log: TextInputEditText? = null
    private var password_log: TextInputEditText? = null
    private var email_log_parent: TextInputLayout? = null
    private var password_log_parent: TextInputLayout? = null
    private var messToast: TextView? = null
    private var login: Button? = null
    private var btnauth: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)
        //ActionBar actionBar = getSupportActionBar();
        // actionBar.hide();

        val layoutInflater = layoutInflater
        val viewgroup = findViewById<ViewGroup>(R.id.custom_layout)
        viewLayout = layoutInflater.inflate(R.layout.custom_toast, viewgroup)
        messToast = viewLayout!!.findViewById<View>(R.id.toast_txt) as TextView
        email_log = findViewById(R.id.log_email)
        password_log = findViewById(R.id.log_password)
        email_log_parent = findViewById(R.id.log_email_parent)
        password_log_parent = findViewById(R.id.log_password_parent)
        login = findViewById<View>(R.id.log_btn) as Button
        login!!.setOnClickListener(this)
        btnauth = findViewById(R.id.log_sign_btn)
        btnauth!!.setOnClickListener(this)
        mAuth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        mAuthListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                val intent = Intent(this@MainLoginActivity, AccountActivity::class.java)
                startActivity(intent)
                Log.e(TAG, "onAuthStateChanged:signed_in:" + user.uid)
            } else {
                Log.e(TAG, "onAuthStateChanged:signed_out")
            }

        }
        //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    private fun updateUI(user:FirebaseUser){
        //to keep connected user
        mAuthListener = AuthStateListener { firebaseAuth ->
          //  user = Firebase.auth!!.currentUser!!
            if (user != null) {
                // User is signed in
                val intent = Intent(this@MainLoginActivity, AccountActivity::class.java)
                startActivity(intent)
            } else {
                Log.e("main","user $user")
                // User is signed out
                // messToast.setText(R.string.mlog_count);
                // messageToast();
            }
        }
    }

    private fun validation() {
        if (email_log!!.text!!.length == 0 ||
                password_log!!.text!!.length == 0) {
            messToast!!.setText(R.string.mlog_tss)
            messageToast()
        } else {
            sigIn(email_log!!.text.toString(), password_log!!.text.toString())
        }
    }

    fun messageToast() {
        val toast1 = Toast.makeText(this@MainLoginActivity, " ", Toast.LENGTH_SHORT)
        toast1.setGravity(Gravity.CENTER, 0, 0)
        toast1.view = viewLayout
        toast1.show()
    }

    private fun sigIn(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        user = mAuth!!.currentUser!!
                        updateUI(user!!)
                        // Sign in success, update UI with the signed-in user's information
                        val intent = Intent(this@MainLoginActivity, AccountActivity::class.java)
                        startActivity(intent)
                        messToast!!.text = "Bonjour " + mAuth!!.currentUser!!.displayName
                        messageToast()
                    } else {
                        // If sign in fails, display a message to the user.
                        //Intent intent = new Intent(MainLoginActivity.this, SignupActivity.class);
                        // startActivity(intent);
                        messToast!!.setText(R.string.mlog_count)
                        messageToast()
                    }
                }
    }

    //keep loginuser connected
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    public override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.log_btn -> validation()
            R.id.log_sign_btn -> {
                val intent = Intent(this@MainLoginActivity, SignupActivity::class.java)
                startActivity(intent)
            }
        }
    }

    companion object {
        private const val PREFS_NAME = "preferences"
        private const val PREF_UNAME = "Username"
        private const val PREF_PASSWORD = "Password"
    }
}