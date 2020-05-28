package com.helenacorp.android.mybibliotheque

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.widget.TextView
import android.os.Bundle
import com.helenacorp.android.mybibliotheque.R
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseUser
import android.content.Intent
import com.helenacorp.android.mybibliotheque.Controllers.Activities.AccountActivity
import android.widget.Toast
import android.view.Gravity
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.helenacorp.android.mybibliotheque.SignupActivity

class MainLoginActivity : AppCompatActivity(), View.OnClickListener {
    private val DefaultUnameValue = ""
    private val DefaultPasswordValue = ""
    private val UnameValue: String? = null
    private val PasswordValue: String? = null
    private var viewLayout: View? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: AuthStateListener? = null
    private var email_log: TextInputEditText? = null
    private var password_log: TextInputEditText? = null
    private var email_log_parent: TextInputLayout? = null
    private var password_log_parent: TextInputLayout? = null
    private var messToast: TextView? = null
    private var login: Button? = null
    private var auth: Button? = null
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
        auth = findViewById(R.id.log_sign_btn)
        auth!!.setOnClickListener(this)
        mAuth = FirebaseAuth.getInstance()

        //to keep connected user
        mAuthListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is signed in
                val intent = Intent(this@MainLoginActivity, AccountActivity::class.java)
                startActivity(intent)
            } else {
                // User is signed out
                // messToast.setText(R.string.mlog_count);
                // messageToast();
            }
        }
        //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    fun validation() {
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