package com.example.flunkystats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.example.flunkystats.database.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

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

        findViewById<Button>(R.id.btnMatches).setOnClickListener {
            val toast = Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG)
            toast.show()
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
                val toast = Toast.makeText(this, "Not yet implemented", Toast.LENGTH_LONG)
                toast.show()
            }
        }
        return true
    }


}