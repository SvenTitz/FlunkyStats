package com.example.flunkystats.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.flunkystats.AppConfig
import com.example.flunkystats.AppConfig.Companion.TAG
import com.example.flunkystats.R
import com.example.flunkystats.database.DataBaseHelper
import com.example.flunkystats.database.FirebaseDatabaseHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class SettingsActivity : AppCompatActivity() {

    private lateinit var tvUser: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnGoogleLogin: SignInButton
    private lateinit var btnTest: Button

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private var account: GoogleSignInAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        tvUser = findViewById(R.id.tv_settings_user)
        btnLogout = findViewById(R.id.btn_settings_logout)
        btnGoogleLogin = findViewById(R.id.btn_settings_google_login)
        btnTest = findViewById(R.id.btn_settings_test)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        account = GoogleSignIn.getLastSignedInAccount(this)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser


        if (currentUser == null) {
            Log.d(TAG, "User is null")
        } else {
            btnLogout.isEnabled = true
            tvUser.text = currentUser.email
            tvUser.setTextColor(ContextCompat.getColor(this, R.color.text_primary_dark))
        }

        if(account == null) {
        } else {
            Log.d(TAG, "Signed in with $account")
            if(firebaseAuth.currentUser == null) {
                Log.d(TAG, "Signed in to firebase with account ${account?.id}")
                Log.d(TAG, account?.idToken!!)
                firebaseAuthWithGoogle(account?.idToken!!)
            }
        }


        btnLogout.setOnClickListener {
            firebaseAuth.signOut()

            googleSignInClient.signOut().addOnCompleteListener(this) {
                updateUIafterLogout()
            }

        }

        btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, AppConfig.RC_SIGN_IN)
        }

        btnTest.setOnClickListener {
            val dbHelper = DataBaseHelper(this)
            val fbDBHelper = FirebaseDatabaseHelper(dbHelper)

            fbDBHelper.testAuth{
                val toast = if (it) {
                    Toast.makeText(this, "You ARE authorized to edit the database", Toast.LENGTH_LONG)
                } else {
                    Toast.makeText(this, "You are NOT authorized to edit the database", Toast.LENGTH_LONG)
                }
                toast.show()
            }
        }

    }

    private fun updateUIafterLogout() {
        btnLogout.isEnabled = false
        tvUser.text = "Nicht Angemeldet"
        tvUser.setTextColor(ContextCompat.getColor(this, R.color.text_mid))

        Log.d(TAG, "Google: $account")
        Log.d(TAG, "Firebase: ${firebaseAuth.currentUser}")
    }

    private fun updateUIafterLogin(user: FirebaseUser?) {
        btnLogout.isEnabled = true
        tvUser.text = user?.email
        tvUser.setTextColor(ContextCompat.getColor(this, R.color.text_primary_dark))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == AppConfig.RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completeTask: Task<GoogleSignInAccount>) {
        try {
            account = completeTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account?.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "Login  with code: " + e.statusCode)
            throw(e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    updateUIafterLogin(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    val toast = Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG)
                    toast.show()
                }

                // ...
            }
    }




}