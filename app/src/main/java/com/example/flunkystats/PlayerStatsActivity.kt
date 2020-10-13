package com.example.flunkystats

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.player_list.*
import kotlinx.android.synthetic.main.player_stats.*
import org.w3c.dom.Text
import java.lang.Error


class PlayerStatsActivity: AppCompatActivity() {

    private val database = Firebase.database
    private val teamMembRef = database.getReference("TeamMembership")
    private val teamsRef = database.getReference("Teams")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_stats)

        var playerID = intent.getStringExtra(AppConfig.EXTRA_MESSAGE_PLAYER_ID)

        if(playerID == null) {
            //throw proper popup error
            Log.w("Sven", "player ID could not be transfered")
            return
        }

        val idText = "ID: $playerID"
        tvPID.text = idText

        readPlayerName(playerID)

        readPlayerTeams(playerID)

//        tvPTeam1.setOnClickListener {
//            val intent = Intent(this, TeamStatsActivity::class.java).apply {
//                putExtra(AppConfig.EXTRA_MESSAGE_TEAM, tvPTeam1.text)
//            }
//            startActivity(intent)
//        }
//        tvPTeam2.setOnClickListener {
//            val intent = Intent(this, TeamStatsActivity::class.java).apply {
//                putExtra(AppConfig.EXTRA_MESSAGE_TEAM, tvPTeam2.text)
//            }
//            startActivity(intent)
//        }
//        tvPTeam3.setOnClickListener {
//            val intent = Intent(this, TeamStatsActivity::class.java).apply {
//                putExtra(AppConfig.EXTRA_MESSAGE_TEAM, tvPTeam3.text)
//            }
//            startActivity(intent)
//        }


    }

    private fun readPlayerName(playerID: String) {
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

    private fun readPlayerTeams(playerID: String) {

        //search for teammemberships of play with ID: $memberID
        val teamMembQ = teamMembRef.orderByChild("memberID").equalTo(playerID)

        //read the teamID for each membership
        teamMembQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                Log.d("Sven", "values: $values")
                values.forEach { (k, v) ->
                    val teamID = v["teamID"]
                    Log.d("Sven", "teamID:  $teamID")
                    if(teamID == null) {
                        return
                    }
                    //read the team name and create new textview with the name
                    readTeamName(teamID)
                }
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    private fun readTeamName(teamID: String) {
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
                createTeamTextView(teamName)
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    private fun createTeamTextView(teamName: String): TextView {

        val newTV:TextView = TextView.inflate(this, R.layout.stats_text_view, null) as TextView
        newTV.text = teamName
        newTV.id = teamName.hashCode()

        llPTeams.addView(newTV)

        return newTV
    }
}