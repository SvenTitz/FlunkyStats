package com.example.flunkystats.activities

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.flunkystats.AppConfig
import com.example.flunkystats.ui.util.LoadsData
import com.example.flunkystats.R
import com.example.flunkystats.database.DataBaseHelper
import com.example.flunkystats.models.PlayerMatchStatsModel
import com.example.flunkystats.models.PlayerModel
import java.lang.ref.WeakReference

class MatchStatsActivity : AppCompatActivity(), LoadsData {

    private lateinit var matchID: String
    private lateinit var pgsBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_stats)

        matchID = intent.getStringExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID) ?: return

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        pgsBar = addProgressBar(findViewById(R.id.cl_m_stats_root), this)
        val asyncTask = DatabaseAsyncTask(this)
        asyncTask.execute(matchID)
    }

    private fun writeStats(models: List<PlayerMatchStatsModel>) {
        findViewById<TextView>(R.id.tv_card_matches_team1).text = models[0].teamName
        findViewById<TextView>(R.id.tv_card_matches_team2).text = models[2].teamName

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


    private class DatabaseAsyncTask(matchStatsActivity: MatchStatsActivity): AsyncTask<String, Int, List<PlayerMatchStatsModel>>() {

        private var activityWeakRef: WeakReference<MatchStatsActivity>? = null

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

            val dbHelper = DataBaseHelper(activity)

            val teamsList = dbHelper.getMatchesTeams(matchID[0]!!)

            val playerList = dbHelper.getTeamsPlayers(teamsList[0].teamID) as ArrayList<PlayerModel>
            val team2Players = dbHelper.getTeamsPlayers(teamsList[1].teamID)
            playerList.addAll(team2Players)

            for( i in 0 until 4) {
                val playerModel = dbHelper.getPlayerMatchStats(playerList[i].playerID, matchID[0]!!)
                playerModel.teamName = teamsList[i/2].teamName
                resList.add(playerModel)
            }

            return resList
        }

        override fun onPostExecute(result: List<PlayerMatchStatsModel>?) {
            val activity: MatchStatsActivity? = activityWeakRef?.get()
            if (activity == null || activity.isFinishing || result == null) return

            activity.writeStats(result)
        }
    }
}