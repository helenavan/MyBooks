package com.helenacorp.android.mybibliotheque

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.helenacorp.android.mybibliotheque.Controllers.Activities.AccountActivity
import com.helenacorp.android.mybibliotheque.model.User
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity(), View.OnClickListener {
    private val pattern = Pattern.compile(EMAIL_PATTERN)
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private val ref: DatabaseReference? = null
    private val mAuthListener: AuthStateListener? = null
    private var authName: TextInputEditText? = null
    private var authEmail: TextInputEditText? = null
    private var authPassw: TextInputEditText? = null
    private lateinit var mFirestore:FirebaseFirestore
    private var authNameParent: TextInputLayout? = null
    private var authEmailParent: TextInputLayout? = null
    private var authPassParent: TextInputLayout? = null
    private var btnCreatAccount: Button? = null
    private var displayName: String? = null
    private val useremail: String? = null
    private val password: String? = null
    private val name: String? = null
    private var viewLayout: View? = null
    private var messToast: TextView? = null
    private var txtTitle: TextView? = null

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val layoutInflater = layoutInflater
        val viewgroup = findViewById<ViewGroup>(R.id.custom_layout)
        viewLayout = layoutInflater.inflate(R.layout.custom_toast, viewgroup)
        messToast = viewLayout!!.findViewById<View>(R.id.toast_txt) as TextView
        txtTitle = findViewById(R.id.txt_signup)
        authName = findViewById(R.id.name_signup)
        authEmail = findViewById(R.id.email_signup)
        authPassw = findViewById(R.id.password_signup)
        authNameParent = findViewById(R.id.name_singup_parent)
        authEmailParent = findViewById(R.id.email_signup_parent)
        authPassParent = findViewById(R.id.password_signup_parent)
        btnCreatAccount = findViewById<View>(R.id.btn_creatAccount) as Button
        btnCreatAccount!!.setOnClickListener(this)
        authName!!.addTextChangedListener(MyTextWatcher(authName))
        authEmail!!.addTextChangedListener(MyTextWatcher(authEmail))
        authPassw!!.addTextChangedListener(MyTextWatcher(authPassw))
        mAuth = FirebaseAuth.getInstance()
        mFirestore = Firebase.firestore
    }

    fun validation() {
        if (!validateName()) {
            return
        }
        if (!validateEmail()) {
            return
        }
        if (!validatePassword()) {
            return
        }
        loginAccount(authName!!.text.toString().also { displayName = it }, authEmail!!.text.toString(), authPassw!!.text.toString())
    }

    private fun loginAccount(name: String, email: String, password: String) {
        // START create_user_with_email
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        messToast!!.setText(R.string.mlog_bvn)
                        messageToast()
                        user = mAuth!!.currentUser
                        val users = User(user!!.uid,displayName, email,null,null)
                        mFirestore.collection("users").document().set(users)
                        val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build()
                        user!!.updateProfile(profileUpdates).addOnCompleteListener {
                            messToast!!.setText(R.string.mlog_bvn)
                            messageToast()
                            val intent = Intent(this@SignupActivity, AccountActivity::class.java)
                            finish()
                            startActivity(intent)
                        }
                    } else {
                        messToast!!.setText(R.string.mlog_signup)
                        messageToast()
                        // If sign in fails, display a message to the user.
                    }
                }
    }

    fun messageToast() {
        val toast1 = Toast.makeText(this@SignupActivity, " ", Toast.LENGTH_SHORT)
        toast1.setGravity(Gravity.CENTER, 0, 0)
        toast1.view = viewLayout
        toast1.show()
    }

    private fun validateName(): Boolean {
        if (authName!!.text.toString().trim { it <= ' ' }.length < 3) {
            authNameParent!!.error = getString(R.string.error_name)
            requestFocus(authName)
            return false
        } else {
            authNameParent!!.isErrorEnabled = false
        }
        return true
    }

    private fun validatePassword(): Boolean {
        if (authPassw!!.text.toString().trim { it <= ' ' }.length < 6) {
            authPassParent!!.error = getString(R.string.error_password)
            requestFocus(authPassw)
            return false
        } else {
            authPassParent!!.isErrorEnabled = false
        }
        return true
    }

    fun validateEmail(): Boolean {
        val email = authEmail!!.text.toString().trim { it <= ' ' }
        if (email.isEmpty() || !isValidEmail(email)) {
            authEmailParent!!.error = getString(R.string.error_email)
            requestFocus(authEmail)
            return false
        } else {
            authEmailParent!!.isErrorEnabled = false
        }
        return true
    }

    private fun requestFocus(view: View?) {
        if (view!!.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
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
    override fun onClick(view: View) {
        if (view === btnCreatAccount) {
            validation()
        }
    }

    private inner class MyTextWatcher(private val view: View?) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            when (view!!.id) {
                R.id.name_signup -> validateName()
                R.id.email_signup -> validateEmail()
                R.id.password_signup -> validatePassword()
            }
        }

    }

    companion object {
        private const val EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$"
        private fun isValidEmail(email: String): Boolean {
            return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }
}