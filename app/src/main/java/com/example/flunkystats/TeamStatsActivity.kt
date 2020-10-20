package com.example.flunkystats

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.example.flunkystats.util.StringUtil
import kotlinx.android.synthetic.main.activity_team_stats.view.*

class TeamStatsActivity: StatsActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_stats)

        val teamID = intent.getStringExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID)

        if(teamID == null) {
            //TODO: throw proper error
            Log.w("Sven", "team ID could not be transferred")
            return
        }

        val idText = "ID: $teamID"
        findViewById<TextView>(R.id.tvTID).text = idText

        loadTeamName(teamID)

        val playerIDs = loadTeamPlayers(teamID)

        loadTeamHitRatio(teamID)

        loadPlayersHitRatio(playerIDs, teamID)

        loadTeamAvgSlugs(teamID)

        loadPlayersAvgSlugs(playerIDs, teamID)

        loadTeamMatchStats(teamID)

        loadTeamTournStats(teamID)
    }


    private fun loadTeamName(teamID: String) {
        val name = dbHelper.getTeamName(teamID)
        findViewById<TextView>(R.id.tvTName).text = name
    }

    private fun loadTeamPlayers(teamID: String): List<String> {
        val playerMap = dbHelper.getTeamsPlayers(teamID)

        playerMap.forEach { (id, name) ->
            val formatName = StringUtil.newLineEachWord(name)
            createTextView(formatName, id, findViewById<LinearLayout>(R.id.llTPlayers), 24F)
        }

        return playerMap.keys.toList()
    }

    private fun loadTeamHitRatio(teamID: String) {
        val ratio = dbHelper.getTeamHitRatio(teamID)
        val ratioFormat = String.format(AppConfig.FLOAT_FORMAT_1, ratio*100) + "%"
        findViewById<TextView>(R.id.tvTHits).text = ratioFormat
    }

    private fun loadPlayersHitRatio(playerIDs: List<String>, teamID: String) {
        val ratio1 = dbHelper.getPlayerHitRatio(playerIDs[0], listOf(teamID), null)
        val ratio2 = dbHelper.getPlayerHitRatio(playerIDs[1], listOf(teamID), null)
        val ratio1Format = String.format(AppConfig.FLOAT_FORMAT_1, ratio1*100) + "%"
        val ratio2Format = String.format(AppConfig.FLOAT_FORMAT_1, ratio2*100) + "%"
        findViewById<TextView>(R.id.tvTHits1).text = ratio1Format
        findViewById<TextView>(R.id.tvTHits2).text = ratio2Format
    }

    private fun loadTeamAvgSlugs(teamID: String) {
        val avgSlugs = dbHelper.getTeamAvgSlugs(teamID)
        val avgSlugsFormat = String.format(AppConfig.FLOAT_FORMAT_1, avgSlugs)
        findViewById<TextView>(R.id.tvTSlugs).text = avgSlugsFormat
    }

    private fun loadPlayersAvgSlugs(playerIDs: List<String>, teamID: String) {
        val slugs1 = dbHelper.getPlayerAvgSlugs(playerIDs[0], listOf(teamID), null)
        val slugs2 = dbHelper.getPlayerAvgSlugs(playerIDs[1], listOf(teamID), null)
        val slugs1Format = String.format(AppConfig.FLOAT_FORMAT_1, slugs1)
        val slugs2Format = String.format(AppConfig.FLOAT_FORMAT_1, slugs2)
        findViewById<TextView>(R.id.tvTSlugs1).text = slugs1Format
        findViewById<TextView>(R.id.tvTSlugs2).text = slugs2Format
    }

    private fun loadTeamMatchStats(teamID: String) {
        val stats = dbHelper.getTeamMatchStats(teamID)
        val ratio = stats[1].toFloat() / stats[0].toFloat()
        val ratioFormat = String.format(AppConfig.FLOAT_FORMAT_0, ratio*100) + "%"
        findViewById<TextView>(R.id.tvTGamesTotal).text = stats[0].toString()
        findViewById<TextView>(R.id.tvTGamesWon).text = stats[1].toString()
        findViewById<TextView>(R.id.tvTGamesWonRatio).text = ratioFormat
    }

    private fun loadTeamTournStats(teamID: String) {
        val stats = dbHelper.getTeamTournStats(teamID)
        findViewById<TextView>(R.id.tvTTurnamentsTotal).text = stats[0].toString()
        findViewById<TextView>(R.id.tvTTurnamentsWon).text = stats[1].toString()
    }
}
