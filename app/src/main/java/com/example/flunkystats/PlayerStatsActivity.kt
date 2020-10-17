package com.example.flunkystats

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_player_stats.*


class PlayerStatsActivity: StatsActivity() {


    //variables used to load team names
    private lateinit var teamPgsBar: ProgressBar
    private var countTeamsLoading = 0
    private var teamNamesList: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_stats)

        teamPgsBar = addProgressBar(findViewById<LinearLayout>(R.id.llPTeams), this)

        val playerID = intent.getStringExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID)

        if(playerID == null) {
           //TODO: throw proper error
            Log.w("Sven", "player ID could not be transfered")
            return
        }

        val idText = "ID: $playerID"
        tvPID.text = idText

        loadPlayerName(playerID, tvPName)

        loadPlayerTeams(playerID, llPTeams)

        val hitRatioView = findViewById<TextView>(R.id.tvPHits)
        val avgSlugsView = findViewById<TextView>(R.id.tvPSlugs)
        loadPlayerMatchStats(playerID, listOf(hitRatioView, avgSlugsView))

        //TODO: Load Rest of Data

    }


    override fun loadPlayerNameCallback(name: String, targetView: View) {
        (targetView as TextView).text = name
    }

    /**
     * Loads all Teams for the player with ID: [playerID]
     */
    private fun loadPlayerTeams(playerID: String, targetLayout: LinearLayout) {
        //add progress bar
        teamPgsBar.visibility = View.VISIBLE

        //search for team memberships of player with ID: [playerID]
        val teamMembQ = teamMembRef.orderByChild("playerID").equalTo(playerID)

        //read the teamID for each membership
        teamMembQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //no teams found
                    Log.w("Sven", "dataSnapshot was null")
                    return
                }
                //loop through all teams found
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                values.forEach { (_, v) ->
                    //read team name and return if null
                    val teamID = v["teamID"] ?: return
                    //read the team name and add it to the teamNames array list
                    countTeamsLoading++
                    loadTeamName(teamID, targetLayout)
                }
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }


    override fun loadTeamNameCallback(teamName: String?, targetView: View) {
        //done loading the team name
        countTeamsLoading--
        if (teamName == null) {
            Log.w("Sven", "Team name is null")
            return
        }
        teamNamesList.add(teamName)

        //create the team text views if $countTeamsLoading is 0
        if (countTeamsLoading == 0) {
            teamPgsBar.visibility = View.GONE
            createTextViews(teamNamesList, targetView)
        }
    }


}