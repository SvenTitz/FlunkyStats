package com.example.flunkystats.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flunkystats.AppConfig
import com.example.flunkystats.AppConfig.Companion.TAG
import com.example.flunkystats.ui.util.LoadsData
import com.example.flunkystats.R
import com.example.flunkystats.database.DataBaseHelper
import com.example.flunkystats.database.FirebaseDatabaseHelper
import com.example.flunkystats.models.*
import java.lang.ref.WeakReference

class MatchStatsActivity : AppCompatActivity(), LoadsData {

    lateinit var dbHelper: DataBaseHelper
    lateinit var fbDbHelper: FirebaseDatabaseHelper
    private lateinit var matchID: String
    private lateinit var pgsBar: ProgressBar
    private lateinit var models: List<PlayerMatchStatsModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_stats)

        matchID = intent.getStringExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID) ?: return

        dbHelper = DataBaseHelper(this)
        fbDbHelper = FirebaseDatabaseHelper(dbHelper)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        pgsBar = addProgressBar(findViewById(R.id.cl_m_stats_root), this)
        updateStats()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_stats_match, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_stats_match_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.menu_stats_match_delete -> {
                fbDbHelper.testAuth {
                    if (it) {
                        openDeleteMatchDialog()
                    } else {
                        val toast =  Toast.makeText(this, "You are NOT authorized to edit the database", Toast.LENGTH_LONG)
                        toast.show()
                    }
                }
            }
            R.id.menu_stats_match_edit -> {
                fbDbHelper.testAuth {
                    if (it) {
                        openEditMatchDialog()
                    } else {
                        val toast =  Toast.makeText(this, "You are NOT authorized to edit the database", Toast.LENGTH_LONG)
                        toast.show()
                    }
                }
            }
        }
        return true
    }

    private fun updateStats() {
        pgsBar.visibility = View.VISIBLE
        val asyncTask = DatabaseAsyncTask(this)
        asyncTask.execute(matchID)
    }

    private fun writeStats(models: List<PlayerMatchStatsModel>) {
        this.models = models
        val teams = dbHelper.getMatchesTeams(matchID)
        findViewById<TextView>(R.id.tv_card_matches_team1).text = teams.getOrNull(0)?.teamName ?: "ERROR"
        findViewById<TextView>(R.id.tv_card_matches_team2).text = teams.getOrNull(1)?.teamName ?: "ERROR"

        findViewById<TextView>(R.id.tv_m_stats_p1).text = models[0].playerName
        findViewById<TextView>(R.id.tv_m_stats_p2).text = models[1].playerName
        findViewById<TextView>(R.id.tv_m_stats_p3).text = models[2].playerName
        findViewById<TextView>(R.id.tv_m_stats_p4).text = models[3].playerName

        findViewById<TextView>(R.id.tv_m_stats_t1).text = models[0].teamName
        findViewById<TextView>(R.id.tv_m_stats_t2).text = models[1].teamName
        findViewById<TextView>(R.id.tv_m_stats_t3).text = models[2].teamName
        findViewById<TextView>(R.id.tv_m_stats_t4).text = models[3].teamName

        findViewById<TextView>(R.id.tv_m_stats_shots1).text = models[0].shots.toString()
        findViewById<TextView>(R.id.tv_m_stats_shots2).text = models[1].shots.toString()
        findViewById<TextView>(R.id.tv_m_stats_shots3).text = models[2].shots.toString()
        findViewById<TextView>(R.id.tv_m_stats_shots4).text = models[3].shots.toString()

        findViewById<TextView>(R.id.tv_m_stats_hits1).text = models[0].hits.toString()
        findViewById<TextView>(R.id.tv_m_stats_hits2).text = models[1].hits.toString()
        findViewById<TextView>(R.id.tv_m_stats_hits3).text = models[2].hits.toString()
        findViewById<TextView>(R.id.tv_m_stats_hits4).text = models[3].hits.toString()

        findViewById<TextView>(R.id.tv_m_stats_slugs1).text = models[0].slugs.toString()
        findViewById<TextView>(R.id.tv_m_stats_slugs2).text = models[1].slugs.toString()
        findViewById<TextView>(R.id.tv_m_stats_slugs3).text = models[2].slugs.toString()
        findViewById<TextView>(R.id.tv_m_stats_slugs4).text = models[3].slugs.toString()

        findViewById<TextView>(R.id.tv_m_stats_winner1).visibility = if (models[0].won) View.VISIBLE else View.INVISIBLE
        findViewById<TextView>(R.id.tv_m_stats_winner2).visibility = if (models[1].won) View.VISIBLE else View.INVISIBLE
        findViewById<TextView>(R.id.tv_m_stats_winner3).visibility = if (models[2].won) View.VISIBLE else View.INVISIBLE
        findViewById<TextView>(R.id.tv_m_stats_winner4).visibility = if (models[3].won) View.VISIBLE else View.INVISIBLE

        pgsBar.visibility = View.GONE

    }

    private fun deleteMatch() {
        fbDbHelper.deleteMatch(matchID)
        dbHelper.deleteMatch(matchID)
        this.finish()
    }

    private fun openDeleteMatchDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
        builder.setTitle("Spiel Löschen")
        builder.setMessage("Bist du sicher, dass du das Spiel löschen willst?")
        builder.setPositiveButton("Löschen") {dialog, _ ->
            deleteMatch()
            Handler().postDelayed( {
                dialog.cancel()
            }, 150)
        }
        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            Handler().postDelayed( {
                dialog.cancel()
            }, 150)
        }

        builder.create().show()
    }

    private fun openEditMatchDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
        val view: View = ConstraintLayout.inflate(this, R.layout.inflatable_dialog_edit_match, null)
        builder.setView(view)
        val dialog = builder.create()

        val tvName = arrayListOf<TextView>(
            view.findViewById<TextView>(R.id.tv_edit_dialog_name1),
            view.findViewById<TextView>(R.id.tv_edit_dialog_name2),
            view.findViewById<TextView>(R.id.tv_edit_dialog_name3),
            view.findViewById<TextView>(R.id.tv_edit_dialog_name4)
        )
        for(i in 0 until tvName.size) {
            tvName[i].text = models[i].playerName
        }

        val cbWon = arrayListOf<CheckBox>(
            view.findViewById<CheckBox>(R.id.cb_edit_dialog_winner1),
            view.findViewById<CheckBox>(R.id.cb_edit_dialog_winner2),
            view.findViewById<CheckBox>(R.id.cb_edit_dialog_winner3),
            view.findViewById<CheckBox>(R.id.cb_edit_dialog_winner4)
        )
        for(i in 0 until cbWon.size) {
            cbWon[i].isChecked = models[i].won
        }

        val etShots = arrayListOf<EditText>(
            view.findViewById<EditText>(R.id.et_edit_dialog_shots1),
            view.findViewById<EditText>(R.id.et_edit_dialog_shots2),
            view.findViewById<EditText>(R.id.et_edit_dialog_shots3),
            view.findViewById<EditText>(R.id.et_edit_dialog_shots4)
        )
        for(i in 0 until etShots.size) {
            etShots[i].setText(models[i].shots.toString())
        }

        val etHits = arrayListOf<EditText>(
            view.findViewById<EditText>(R.id.et_edit_dialog_hits1),
            view.findViewById<EditText>(R.id.et_edit_dialog_hits2),
            view.findViewById<EditText>(R.id.et_edit_dialog_hits3),
            view.findViewById<EditText>(R.id.et_edit_dialog_hits4)
        )
        for(i in 0 until etHits.size) {
            etHits[i].setText(models[i].hits.toString())
        }

        val etSlugs = arrayListOf<EditText>(
            view.findViewById<EditText>(R.id.et_edit_dialog_slugs1),
            view.findViewById<EditText>(R.id.et_edit_dialog_slugs2),
            view.findViewById<EditText>(R.id.et_edit_dialog_slugs3),
            view.findViewById<EditText>(R.id.et_edit_dialog_slugs4)
        )
        for(i in 0 until etSlugs.size) {
            etSlugs[i].setText(models[i].slugs.toString())
        }

        for (i in 0 until etSlugs.size) {
            if(models[i].playerID == null) {
                cbWon[i].isEnabled = false
                etShots[i].isEnabled = false
                etHits[i].isEnabled = false
                etSlugs[i].isEnabled = false
            }
        }

        /*
        cbWon1.setOnClickListener {
            cbWon2.isChecked = cbWon1.isChecked
            cbWon3.isChecked = !cbWon1.isChecked
            cbWon4.isChecked = !cbWon1.isChecked
        }
        cbWon2.setOnClickListener {
            cbWon1.isChecked = cbWon2.isChecked
            cbWon3.isChecked = !cbWon2.isChecked
            cbWon4.isChecked = !cbWon2.isChecked
        }
        cbWon3.setOnClickListener {
            cbWon4.isChecked = cbWon3.isChecked
            cbWon1.isChecked = !cbWon3.isChecked
            cbWon2.isChecked = !cbWon3.isChecked
        }
        cbWon4.setOnClickListener {
            cbWon3.isChecked = cbWon4.isChecked
            cbWon1.isChecked = !cbWon4.isChecked
            cbWon2.isChecked = !cbWon4.isChecked
        } */

        view.findViewById<Button>(R.id.btn_edit_dialog_cacel).setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({
                dialog.cancel()
            }, 150)
        }

        view.findViewById<Button>(R.id.btn_edit_dialog_ok).setOnClickListener {

            val pModels = arrayListOf<MatchPlayerPairModel>()

            for(i in models.indices) {
                pModels.add(MatchPlayerPairModel(
                    matchPlayerPairID = models[i].matchPlayerID,
                    matchID = matchID,
                    playerID = models[i].playerID,
                    shots = etShots[i].text.toString().toInt(),
                    hits = etHits[i].text.toString().toInt(),
                    slugs = etSlugs[i].text.toString().toInt(),
                    won = cbWon[i].isChecked
                ))
            }

            /*
            val p1Model = MatchPlayerPairModel(
                matchPlayerPairID = models[0].matchPlayerID,
                matchID = matchID,
                playerID = models[0].playerID,
                shots = etShots1.text.toString().toInt(),
                hits = etHits1.text.toString().toInt(),
                slugs = etSlugs1.text.toString().toInt(),
                won = cbWon1.isChecked
            )
            val p2Model = MatchPlayerPairModel(
                matchPlayerPairID = models[1].matchPlayerID,
                matchID = matchID,
                playerID = models[1].playerID,
                shots = etShots2.text.toString().toInt(),
                hits = etHits2.text.toString().toInt(),
                slugs = etSlugs2.text.toString().toInt(),
                won = cbWon2.isChecked
            )
            val p3Model = MatchPlayerPairModel(
                matchPlayerPairID = models[2].matchPlayerID,
                matchID = matchID,
                playerID = models[2].playerID,
                shots = etShots3.text.toString().toInt(),
                hits = etHits3.text.toString().toInt(),
                slugs = etSlugs3.text.toString().toInt(),
                won = cbWon3.isChecked
            )
            val p4Model = MatchPlayerPairModel(
                matchPlayerPairID = models[3].matchPlayerID,
                matchID = matchID,
                playerID = models[3].playerID,
                shots = etShots4.text.toString().toInt(),
                hits = etHits4.text.toString().toInt(),
                slugs = etSlugs4.text.toString().toInt(),
                won = cbWon4.isChecked
            )
            */

            pModels.forEach{
                editMatch(it)
            }

            val teams = dbHelper.getMatchesTeams(matchID)

            if(teams.isNotEmpty()) {
                val tModel = MatchTeamPairModel(
                    matchTeamPairID = teams[0].teamID?.let { it1 -> dbHelper.getMatchTeamID(matchID, it1) },
                    matchID = matchID,
                    teamID = teams[0].teamID,
                    won = didTeamWin(teams[0].teamID, pModels)
                )
                editMatch(tModel)
            }
            if(teams.size > 1) {
                val tModel = MatchTeamPairModel(
                    matchTeamPairID = teams[1].teamID?.let { it1 -> dbHelper.getMatchTeamID(matchID, it1) },
                    matchID = matchID,
                    teamID = teams[1].teamID,
                    won = didTeamWin(teams[1].teamID, pModels)
                )
                editMatch(tModel)
            }


            Handler(Looper.getMainLooper()).postDelayed({
                updateStats()
                dialog.cancel()
            }, 150)
        }

        dialog.show()
    }

    private fun didTeamWin(teamID: String?, pModels: ArrayList<MatchPlayerPairModel>): Boolean {
        if (teamID == null)
            return false


        pModels.forEach {
            if(it.playerID != null && dbHelper.getPlayersTeamInMatch(it.playerID!!, matchID).teamID == teamID && it.won)
                return true
        }
        return false
    }

    private fun editMatch(mpModel: MatchPlayerPairModel) {
        if(mpModel.matchID == null || mpModel.playerID == null || mpModel.matchPlayerPairID == null) {
            return
        }
        Log.d(TAG, "Edited ${dbHelper.getPlayerName(mpModel.playerID!!)} with shots: ${mpModel.shots}, hit: ${mpModel.hits}, slugs: ${mpModel.slugs}, won: ${mpModel.won}")
        fbDbHelper.updateMatchPlayerStats(mpModel)
        dbHelper.updateMatchPlayerStats(mpModel)
    }

    private fun editMatch(mtModel: MatchTeamPairModel) {
        if(mtModel.matchID == null || mtModel.teamID == null || mtModel.matchTeamPairID == null) {
            return
        }
        Log.d(TAG, "Edited: ${dbHelper.getTeamName(mtModel.teamID!!)} with won: ${mtModel.won}")
        fbDbHelper.updateMatchTeamStats(mtModel)
        dbHelper.updateMatchTeamStats(mtModel)
    }

    private class DatabaseAsyncTask(matchStatsActivity: MatchStatsActivity): AsyncTask<String, Int, List<PlayerMatchStatsModel>>() {

        private var activityWeakRef: WeakReference<MatchStatsActivity>? = null
        private lateinit var dbHelper: DataBaseHelper

        init {
            activityWeakRef = WeakReference<MatchStatsActivity>(matchStatsActivity)
        }

        override fun doInBackground(vararg matchID: String?): List<PlayerMatchStatsModel> {

            val resList = arrayListOf<PlayerMatchStatsModel>()

            if(matchID.isEmpty() || matchID[0] == null) return resList

            val activity: MatchStatsActivity? = activityWeakRef?.get()
            if (activity == null || activity.isFinishing) {
                return resList
            }

            dbHelper = activity.dbHelper

            //val teamsList = arrayListOf<TeamModel>()
           // teamsList.addAll(dbHelper.getMatchesTeams(matchID[0]!!))
            val playerList = arrayListOf<PlayerModel>()
            playerList.addAll(dbHelper.getMatchesPlayers(matchID[0]!!))

            //for (i in teamsList.size until 2) {
                //teamsList.add(TeamModel(teamID = null, teamName = null))
            //}
            for (i in playerList.size until 4) {
                playerList.add(PlayerModel(null, null))
            }




            for( i in 0 until playerList.size) {
                var playerMatchModel = PlayerMatchStatsModel(null, "Entferner Spieler", 0, 0, 0, false)
                if (playerList[i].playerID != null) {
                    val playerID = playerList[i].playerID!!
                    playerMatchModel = dbHelper.getPlayerMatchStats(playerID, matchID[0]!!)
                    val teamModel = dbHelper.getPlayersTeamInMatch(playerID, matchID[0]!!)
                    playerMatchModel.teamID = teamModel.teamID
                    playerMatchModel.teamName = teamModel.teamName
                    playerMatchModel.matchTeamID = teamModel.teamID?.let { dbHelper.getMatchTeamID(matchID[0]!!, it) }
                }
                //playerModel.teamName = teamsList[i/2].teamName
                //playerModel.teamID = teamsList[i/2].teamID
                //playerModel.matchTeamID = dbHelper.getMatchTeamID(matchID[0]!!, teamsList[i/2].teamID)
                resList.add(playerMatchModel)
            }

            return resList.sortedBy { !it.won }
        }

        private fun findMissingPlayers(teamID: String, matchID: String) {
        }

        override fun onPostExecute(result: List<PlayerMatchStatsModel>?) {
            val activity: MatchStatsActivity? = activityWeakRef?.get()
            if (activity == null || activity.isFinishing || result == null) return

            activity.writeStats(result)
        }
    }
}