package com.example.flunkystats.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flunkystats.AppConfig
import com.example.flunkystats.R
import com.example.flunkystats.models.FilterListItemModel
import com.example.flunkystats.models.PlayerModel
import com.example.flunkystats.util.StringUtil
import java.util.*
import kotlin.collections.ArrayList

class TeamStatsActivity: StatsActivity() {

    private lateinit var teamID: String
    private lateinit var players: List<PlayerModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        teamID = intent.getStringExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID) ?: return
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_stats)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val idText = "ID: $teamID"
        findViewById<TextView>(R.id.tvTID).text = idText

        loadTeamName()

        players = loadTeamPlayers()

        loadTeamHitRatio()

        loadPlayersHitRatio()

        loadTeamAvgSlugs()

        loadPlayersAvgSlugs()

        loadTeamMatchStats()

        loadTeamTournStats()
    }


    private fun loadTeamName() {
        val name = dbHelper.getTeamName(teamID)
        findViewById<TextView>(R.id.tv_t_stats_name).text = name
    }

    private fun loadTeamPlayers(): List<PlayerModel> {
        val players = dbHelper.getTeamsPlayers(teamID)
        val tvPlayer1 = findViewById<TextView>(R.id.tv_t_stats_player1)
        val tvPlayer2 = findViewById<TextView>(R.id.tv_t_stats_player2)
        val clickListener = View.OnClickListener {
            val intent = Intent(this, PlayerStatsActivity::class.java).apply {
                putExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID, it.tag as String)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
        }

        val p1Name = players[0].playerName?.let { StringUtil.newLineEachWord(it) }
        tvPlayer1.text = p1Name
        tvPlayer1.tag = players[0].playerID
        tvPlayer1.setOnClickListener(clickListener)

        val p2Name = players[1].playerName?.let { StringUtil.newLineEachWord(it) }
        tvPlayer2.text = p2Name
        tvPlayer2.tag = players[1].playerID
        tvPlayer2.setOnClickListener(clickListener)

        return players
    }

    private fun loadTeamHitRatio(filterTournIDs: List<String>? = null) {
        val ratio = if(filterTournIDs == null) {
            dbHelper.getTeamHitRatio(teamID)
        } else {
            dbHelper.getTeamHitRatio(teamID, filterTournIDs)
        }

        val ratioFormat = String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_1, ratio*100) + "%"
        findViewById<TextView>(R.id.tv_t_stats_hit_ratio_t).text = ratioFormat
    }

    private fun loadPlayersHitRatio(filterTournIDs: List<String>? = null) {
        val ratio1 = dbHelper.getPlayerHitRatio(players[0].playerID, listOf(teamID), filterTournIDs)
        val ratio2 = dbHelper.getPlayerHitRatio(players[1].playerID, listOf(teamID), filterTournIDs)
        val ratio1Format = String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_1, ratio1*100) + "%"
        val ratio2Format = String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_1, ratio2*100) + "%"
        findViewById<TextView>(R.id.tv_t_stats_hit_ratio_p1).text = ratio1Format
        findViewById<TextView>(R.id.tv_t_stats_hit_ratio_p2).text = ratio2Format
    }

    private fun loadTeamAvgSlugs(filterTournIDs: List<String>? = null) {
        val avgSlugs = if(filterTournIDs == null) {
            dbHelper.getTeamAvgSlugs(teamID)
        } else {
            dbHelper.getTeamAvgSlugs(teamID, filterTournIDs)
        }
        val avgSlugsFormat = String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_1, avgSlugs)
        findViewById<TextView>(R.id.tv_t_stats_slugs_t).text = avgSlugsFormat
    }

    private fun loadPlayersAvgSlugs(filterTournIDs: List<String>? = null) {
        val slugs1 = dbHelper.getPlayerAvgSlugs(players[0].playerID, listOf(teamID), filterTournIDs)
        val slugs2 = dbHelper.getPlayerAvgSlugs(players[1].playerID, listOf(teamID), filterTournIDs)
        val slugs1Format = String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_1, slugs1)
        val slugs2Format = String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_1, slugs2)
        findViewById<TextView>(R.id.tv_t_stats_slugs_p1).text = slugs1Format
        findViewById<TextView>(R.id.tv_t_stats_slugs_p2).text = slugs2Format
    }

    private fun loadTeamMatchStats() {
        val stats = dbHelper.getTeamMatchStats(teamID)
        val ratio = stats[1].toFloat() / stats[0].toFloat()
        val ratioFormat = String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_0, ratio*100) + "%"
        findViewById<TextView>(R.id.tv_t_stats_matches_total).text = stats[0].toString()
        findViewById<TextView>(R.id.tv_t_stats_matches_won).text = stats[1].toString()
        findViewById<TextView>(R.id.tv_t_stats_matches_ratio).text = ratioFormat
    }

    private fun loadTeamTournStats() {
        val stats = dbHelper.getTeamTournStats(teamID)
        findViewById<TextView>(R.id.tv_t_stats_tourn_total).text = stats[0].toString()
        findViewById<TextView>(R.id.tv_t_stats_tourn_won).text = stats[1].toString()
    }

    override fun loadTournFilterData(): ArrayList<FilterListItemModel> {
        val tournMap = dbHelper.getTeamsTourns(teamID)
        val resList: ArrayList<FilterListItemModel> = arrayListOf()

        tournMap.forEach {
            val item = FilterListItemModel(id = it.tournID, name = it.name ?: "ERROR")
            resList.add(item)
        }

        return resList
    }

    override fun openFilterAlertDialog(item: MenuItem) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
        builder.setTitle(item.title)

        val view:View = ConstraintLayout.inflate(this, R.layout.inflatable_dialog_filter_t, null)

        buildFilterRecView(R.id.rv_Tourns,tournFilterData, view)

        builder.setView(view)

        val dialog = builder.create()

        view.findViewById<Button>(R.id.btn_filter_dialog_cacel).setOnClickListener {
            Handler().postDelayed( {
                dialog.cancel()
            }, 150)
        }

        view.findViewById<Button>(R.id.btn_filter_dialog_ok).setOnClickListener {

            val filterTournIDs: ArrayList<String> = arrayListOf()
            tournFilterData.forEach{
                if(it.checked) {
                    filterTournIDs.add(it.id)
                }
            }

            loadTeamHitRatio(filterTournIDs)
            loadPlayersHitRatio(filterTournIDs)
            loadTeamAvgSlugs(filterTournIDs)
            loadPlayersAvgSlugs(filterTournIDs)

            Log.d("Sven", "OK clicked")
            Handler().postDelayed( {
                dialog.cancel()
            }, 150)
        }

        dialog.show()
    }

}
