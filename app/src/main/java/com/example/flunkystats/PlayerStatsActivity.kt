package com.example.flunkystats

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_player_stats.*


class PlayerStatsActivity: AppCompatActivity(), LoadsData {

    private val database = Firebase.database
    private val teamMembRef = database.getReference("TeamMembership")
    private val teamsRef = database.getReference("Teams")

    private lateinit var teamPgsBar: ProgressBar
    private var countTeamsLoading = 0;
    private var teamNames: ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_stats)

        teamPgsBar = addProgressBar(findViewById<LinearLayout>(R.id.llPTeams), this)

        var playerID = intent.getStringExtra(AppConfig.EXTRA_MESSAGE_PLAYER_ID)

        if(playerID == null) {
            //throw proper popup error
            //throw proper popup error
            Log.w("Sven", "player ID could not be transfered")
            return
        }

        val idText = "ID: $playerID"
        tvPID.text = idText

        loadPlayerName(playerID)

        loadPlayerTeams(playerID)

    }

    private fun loadPlayerName(playerID: String) {
        val playerRef = database.getReference("Players")
        val playerQuery = playerRef.orderByKey().equalTo(playerID)
        playerQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                Log.d("Sven", "values: $values")
                val entry = values.iterator().next()
                Log.d("Sven", "entry: $entry")
                Log.d("Sven", "name: ${entry.value["name"]}")
                tvPName.text = entry.value["name"]
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    private fun loadPlayerTeams(playerID: String) {
        //add progress bar
        teamPgsBar.visibility = View.VISIBLE

        //search for teammemberships of play with ID: $memberID
        val teamMembQ = teamMembRef.orderByChild("memberID").equalTo(playerID)

        //read the teamID for each membership
        teamMembQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value == null) {
                    Log.w("Sven", "dataSnapshot was null")
                    return
                }
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                Log.d("Sven", "values: $values")
                values.forEach { (k, v) ->
                    val teamID = v["teamID"]
                    Log.d("Sven", "teamID:  $teamID")
                    if(teamID == null) {
                        return
                    }
                    //read the team name and add it to the teamNames array list
                    loadTeamName(teamID)
                }
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    private fun loadTeamName(teamID: String) {
        countTeamsLoading++

        val teamQ = teamsRef.orderByKey().equalTo(teamID)
        teamQ.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                Log.d("Sven", "values: $values")
                val entry = values.iterator().next()
                Log.d("Sven", "entry: $entry")
                Log.d("Sven", "name: ${entry.value["name"]}")
                val teamName = entry.value["name"]
                if (teamName == null) {
                    Log.w("Sven", "Team name is null")
                    return
                }
                teamNames.add(teamName)
                countTeamsLoading--
                createTeamTextViews()
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    private fun createTeamTextViews() {
        if(countTeamsLoading != 0) {
            return
        }
        else {
            teamPgsBar.visibility = View.GONE
            teamNames.forEach { name ->
                createTeamTextView(name)
            }
        }
    }

    private fun createTeamTextView(teamName: String): TextView {

        val newTV:TextView = TextView.inflate(this, R.layout.inflatable_stats_text_view, null) as TextView
        newTV.text = teamName
        newTV.id = teamName.hashCode()

        llPTeams.addView(newTV)

        return newTV
    }
}