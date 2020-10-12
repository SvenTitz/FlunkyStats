package com.example.flunkystats

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.player_stats.*

const val EXTRA_MESSAGE_TEAM = "com.example.flunkystats.TEAM"

class PlayerStatsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_stats)

        tvPTeam1.setOnClickListener {
            val intent = Intent(this, TeamStatsActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE_TEAM, tvPTeam1.text)
            }
            startActivity(intent)
        }
        tvPTeam2.setOnClickListener {
            val intent = Intent(this, TeamStatsActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE_TEAM, tvPTeam2.text)
            }
            startActivity(intent)
        }
        tvPTeam3.setOnClickListener {
            val intent = Intent(this, TeamStatsActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE_TEAM, tvPTeam3.text)
            }
            startActivity(intent)
        }


    }
}