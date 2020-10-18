package com.example.flunkystats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set on click listener for Players Button
        findViewById<Button>(R.id.btnPlayers).setOnClickListener {
            startActivity(Intent(this, PlayerListActivity::class.java))
        }

        //set on click listener for Teams button
        findViewById<Button>(R.id.btnTeams).setOnClickListener {
            startActivity(Intent(this, TeamListActivity::class.java))
        }

        findViewById<Button>(R.id.btnMatches).setOnClickListener {
            val snack = Snackbar.make(it, "Not yet implemented", Snackbar.LENGTH_LONG)
            snack.show()
        }

        findViewById<Button>(R.id.btnTurnaments).setOnClickListener {
            val snack = Snackbar.make(it, "Not yet implemented", Snackbar.LENGTH_LONG)
            snack.show()
        }

    }
}