package com.example.flunkystats.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.example.flunkystats.AppConfig
import com.example.flunkystats.R
import com.example.flunkystats.database.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        firebaseAuth = FirebaseAuth.getInstance()
        Log.d("Sven", "firebase auth current use: ${firebaseAuth.currentUser?.uid}")

        val dbHelper = DataBaseHelper(this)
        val fbDBHelper = FirebaseDatabaseHelper(dbHelper)

        //check if database is up to date
        fbDBHelper.checkUpToDate {
            if (it) {
                //database is up to date
                Log.d("Sven", "Database IS up-to-date")
            } else {
                //database is not up to date
                //TODO: reload only the part that is not up to date
                Log.d("Sven", "Database is NOT up-to-date")
                fbDBHelper.reloadEntireDatabase()
            }
        }

        //set on click listener for Players Button
        findViewById<Button>(R.id.btnPlayers).setOnClickListener {
            ViewCompat.setElevation(it, 0F)
            startActivity(Intent(this, PlayerListActivity::class.java))
        }

        //set on click listener for Teams button
        findViewById<Button>(R.id.btnTeams).setOnClickListener {
            startActivity(Intent(this, TeamListActivity::class.java))
        }

        findViewById<Button>(R.id.btnRankings).setOnClickListener {
            startActivity(Intent(this, RankingsActivity::class.java))
        }

        findViewById<Button>(R.id.btnMatches).setOnClickListener {
            startActivity(Intent(this, MatchesListActivity::class.java))
        }

        findViewById<Button>(R.id.btnTurnaments).setOnClickListener {
            val toast = Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG)
            toast.show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return true
    }


}