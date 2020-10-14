package com.example.flunkystats

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TeamStatsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_stats)

        val team = intent.getStringExtra(AppConfig.EXTRA_MESSAGE_TEAM)

        if(team != null) {
            Log.d("Sven", team.toString())

            findViewById<TextView>(R.id.tvTName).apply {
                text = team
            }
        }

    }

}