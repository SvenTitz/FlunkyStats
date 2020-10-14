package com.example.flunkystats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set on click listener for Players Button
        btnPlayers.setOnClickListener {
            startActivity(Intent(this, PlayerListActivity::class.java))
        }

        //set on click listener for Teams button
        btnTeams.setOnClickListener {
            //TODO: to teams list, not stats
            startActivity(Intent(this, TeamStatsActivity::class.java))
        }

        //TODO: Turnier button

    }
}