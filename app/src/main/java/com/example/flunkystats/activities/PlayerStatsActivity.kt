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
import java.util.*
import kotlin.collections.ArrayList


class PlayerStatsActivity: StatsActivity() {

    private lateinit var playerID: String
    private lateinit var teamFilterData: ArrayList<FilterListItemModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        playerID = intent.getStringExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID) ?: return
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_stats)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        teamFilterData = loadTeamFilterData()

        val idText = "ID: $playerID"
        findViewById<TextView>(R.id.tv_p_stats_id).text = idText

        loadStats()

    }


    private fun loadStats() {
        loadPlayerName()
        loadPlayerTeams()
        loadPlayerHitRatio()
        loadPlayerAvgSlugs()
        loadPlayerMatchStats()
        loadPlayerTournStats()
    }

    private fun loadPlayerName() {
        val name = dbHelper.getPlayerName(playerID)
        findViewById<TextView>(R.id.tv_p_stats_name).text = name
    }

    private fun loadPlayerTeams() {
        val teamsMap = dbHelper.getPlayersTeams(playerID)

        var prevView: TextView? = null
        teamsMap.forEach {
            val teamID = it.teamID
            val tvTeam = createTextView(it.teamName ?: "ERROR", it.teamID, findViewById(R.id.cl_p_stats_teams), prevView)
            prevView = tvTeam
            tvTeam.setOnClickListener {
                //Open stats page of the entry. send entryID as extra message
                val intent = Intent(this, TeamStatsActivity::class.java).apply {
                    putExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID, teamID)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(intent)
            }
        }
    }

    private fun loadPlayerHitRatio(filterTeamIDs: ArrayList<String>? = null, filterTournIDs: ArrayList<String>? = null) {
        val ratio = if (filterTeamIDs == null && filterTournIDs == null) {
            dbHelper.getPlayerHitRatio(playerID)
        } else {
            dbHelper.getPlayerHitRatio(playerID, filterTeamIDs, filterTournIDs)
        }
        val ratioFormat = String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_1, ratio*100) + "%"
        findViewById<TextView>(R.id.tv_p_stats_hits).text = ratioFormat
    }

    private fun loadPlayerAvgSlugs(filterTeamIDs: List<String>? = null, filterTournIDs: List<String>? = null) {
        val avgSlugs = if (filterTeamIDs == null && filterTournIDs == null) {
            dbHelper.getPlayerAvgSlugs(playerID)
        } else {
            dbHelper.getPlayerAvgSlugs(playerID, filterTeamIDs, filterTournIDs)
        }
        val avgSlugsFormat = String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_1, avgSlugs)
        findViewById<TextView>(R.id.tv_p_stats_slugs).text = avgSlugsFormat
    }

    private fun loadPlayerMatchStats(filterTeamIDs: List<String>? = null, filterTournIDs: List<String>? = null) {
        val stats = if (filterTeamIDs == null && filterTournIDs == null) {
            dbHelper.getPlayerMatchNumbers(playerID)
        } else {
            dbHelper.getPlayerMatchNumbers(playerID, filterTeamIDs, filterTournIDs)
        }
        val ratio = stats[1].toFloat() / stats[0].toFloat()
        val ratioFormat = String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_0, ratio*100) + "%"
        findViewById<TextView>(R.id.tv_p_stats_matches_total).text = stats[0].toString()
        findViewById<TextView>(R.id.tv_p_stats_matches_won).text = stats[1].toString()
        findViewById<TextView>(R.id.tv_p_stats_matches_ratio).text = ratioFormat
    }

    private fun loadPlayerTournStats(filterTeamIDs: List<String>? = null, filterTournIDs: List<String>? = null) {
        val stats = if (filterTeamIDs == null && filterTournIDs == null) {
            dbHelper.getPlayerTournStats(playerID)
        } else {
            dbHelper.getPlayerTournStats(playerID, filterTeamIDs, filterTournIDs)
        }
        findViewById<TextView>(R.id.tv_p_stats_tourn_total).text = stats[0].toString()
        findViewById<TextView>(R.id.tv_p_stats_tourn_won).text = stats[1].toString()
    }

    override fun openFilterAlertDialog(item: MenuItem) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
        builder.setTitle(item.title)

        val view:View = ConstraintLayout.inflate(this, R.layout.inflatable_dialog_filter_p, null)

        val tournViewAdapter = buildFilterRecView(R.id.rv_Tourns, tournFilterData, view)
        val teamViewAdapter = buildFilterRecView(R.id.rv_Teams, teamFilterData, view)

        Log.d("Sven", "teams size: ${teamViewAdapter.ctvItemList.size}; tourn size: ${tournViewAdapter.ctvItemList.size}")
        teamViewAdapter.ctvItemList.forEach {
            Log.d("Sven", "team")
            Log.d("Sven", "${it.text}")
        }
        tournViewAdapter.ctvItemList.forEach {
            Log.d("Sven", "tourn")
            Log.d("Sven", "${it.text}")
        }

        builder.setView(view)
        builder.setView(view)

        val dialog = builder.create()

        view.findViewById<Button>(R.id.btn_filter_dialog_cacel).setOnClickListener {
            Handler().postDelayed( {
                dialog.cancel()
            }, 150)
        }

        view.findViewById<Button>(R.id.btn_filter_dialog_ok).setOnClickListener {

            val filterTeamIDs: ArrayList<String> = arrayListOf()
            teamFilterData.forEach{
                if(it.checked) {
                    filterTeamIDs.add(it.id)
                }
            }

            val filterTournIDs: ArrayList<String> = arrayListOf()
            tournFilterData.forEach{
                if(it.checked) {
                    filterTournIDs.add(it.id)
                }
            }

            loadPlayerHitRatio(filterTeamIDs, filterTournIDs)
            loadPlayerAvgSlugs(filterTeamIDs, filterTournIDs)
            loadPlayerMatchStats(filterTeamIDs, filterTournIDs)
            loadPlayerTournStats(filterTeamIDs, filterTournIDs)

            Handler().postDelayed( {
                dialog.cancel()
            }, 150)
        }

        dialog.show()
    }

    override fun loadTournFilterData(): ArrayList<FilterListItemModel> {
        val tournMap = dbHelper.getPlayersTourns(playerID)
        val resList: ArrayList<FilterListItemModel> = arrayListOf()

        tournMap.forEach{
            val item = FilterListItemModel(id = it.tournID, name = it.name ?: "ERROR")
            resList.add(item)
        }

        return resList
    }

    private fun loadTeamFilterData():ArrayList<FilterListItemModel> {
        val teamMap = dbHelper.getPlayersTeams(playerID)
        val resList: ArrayList<FilterListItemModel> = arrayListOf()

        teamMap.forEach{
            val item = FilterListItemModel(id = it.teamID, name = it.teamName ?: "ERROR")
            resList.add(item)
        }

        return resList
    }
}