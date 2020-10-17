package com.example.flunkystats

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.example.flunkystats.util.StringUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_team_stats.*

class TeamStatsActivity: StatsActivity() {

    private lateinit var memberPgsBar: ProgressBar
    private var countPlayersLoading = 0
    private var playerNamesList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_stats)


        memberPgsBar = addProgressBar(findViewById<LinearLayout>(R.id.llTPlayers), this)

        val teamID = intent.getStringExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID)

        if(teamID == null) {
            //TODO: throw proper error
            Log.w("Sven", "team ID could not be transfered")
            return
        }

        val idText = "ID: $teamID"
        findViewById<TextView>(R.id.tvTID).text = idText

        loadTeamName(teamID, findViewById(R.id.tvTName))

        loadTeamPlayers(teamID, findViewById<LinearLayout>(R.id.llTPlayers))
    }


    override fun loadTeamNameCallback(teamName: String?, targetView: View) {
        (targetView as TextView).text = teamName ?: "Error loading team name"
    }



    private fun loadTeamPlayers(teamID: String, targetLayout: LinearLayout) {
        //add progress bar
        memberPgsBar.visibility = View.VISIBLE

        val teamMembQ = teamMembRef.orderByChild("teamID").equalTo(teamID)

        //read the teamID for each membership
        teamMembQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value == null) {
                    //no teams found
                    Log.w("Sven", "dataSnapshot was null")
                    return
                }
                //loop through all players found
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>

                values.forEach { (_, v) ->
                    //read team name and return if null
                    val playerID = v["playerID"] ?: return
                    //read the team name and add it to the teamNames array list
                    countPlayersLoading++
                    loadPlayerName(playerID, targetLayout)
                }

            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }


    override fun loadPlayerNameCallback(name: String, targetView: View) {
        //done loading the player name
        countPlayersLoading--
        if (name == null) {
            Log.w("Sven", "Team name is null")
            return
        }
        playerNamesList.add(StringUtil.newLineEachWord(name))
            //create the team text views if $countTeamsLoading is 0
        if (countPlayersLoading == 0) {
            memberPgsBar.visibility = View.GONE
            createTextViews(playerNamesList, targetView, 24F)
        }
    }

}