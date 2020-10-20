package com.example.flunkystats

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_player_stats.*


class PlayerStatsActivity: StatsActivity() {


    //variables used to load team names
    private lateinit var teamPgsBar: ProgressBar
    private var countTeamsLoading = 0
    private var teamNamesList: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_stats)

        val playerID = intent.getStringExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID)

        if(playerID == null) {
           //TODO: throw proper error
            Log.w("Sven", "player ID could not be transfered")
            return
        }

        val idText = "ID: $playerID"
        tvPID.text = idText

        loadPlayerName(playerID)

        loadPlayerTeams(playerID)

        loadPlayerHitRatio(playerID)

        loadPlayerAvgSlugs(playerID)

        loadPlayerMatchStats(playerID)

        loadPlayerTournStats(playerID)
    }


    private fun loadPlayerName(playerID: String) {
        val name = dbHelper.getPlayerName(playerID)
        findViewById<TextView>(R.id.tvPName).text = name
    }

    private fun loadPlayerTeams(playerID: String) {
        val teamsMap = dbHelper.getPlayersTeams(playerID) ?: return

        teamsMap.forEach { (id, name) ->
            createTextView(name, id, findViewById(R.id.llPTeams))
        }
    }

    private fun loadPlayerHitRatio(playerID: String) {
        val ratio = dbHelper.getPlayerHitRatio(playerID)
        val ratioFormat = String.format(AppConfig.FLOAT_FORMAT_1, ratio*100) + "%"
        findViewById<TextView>(R.id.tvPHits).text = ratioFormat
    }

    private fun loadPlayerAvgSlugs(playerID: String) {
        val avgSlugs = dbHelper.getPlayerAvgSlugs(playerID)
        val avgSlugsFormat = String.format(AppConfig.FLOAT_FORMAT_1, avgSlugs)
        findViewById<TextView>(R.id.tvPSlugs).text = avgSlugsFormat
    }

    private fun loadPlayerMatchStats(playerID: String) {
        val stats = dbHelper.getPlayerMatchStats(playerID)
        val ratio = stats[1].toFloat() / stats[0].toFloat()
        val ratioFormat = String.format(AppConfig.FLOAT_FORMAT_0, ratio*100) + "%"
        findViewById<TextView>(R.id.tvPGamesTotal).text = stats[0].toString()
        findViewById<TextView>(R.id.tvPGamesWon).text = stats[1].toString()
        findViewById<TextView>(R.id.tvPGamesWonRatio).text = ratioFormat
    }

    private fun loadPlayerTournStats(playerID: String) {
        val stats = dbHelper.getPlayerTournStats(playerID)
        findViewById<TextView>(R.id.tvPTurnamentsTotal).text = stats[0].toString()
        findViewById<TextView>(R.id.tvPTurnamentsWon).text = stats[1].toString()
    }
}