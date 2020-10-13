package com.example.flunkystats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.flunkystats.data.TeamData
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPlayers.setOnClickListener {
            startActivity(Intent(this, PlayerListActivity::class.java))
        }

        btnTeams.setOnClickListener {
            startActivity(Intent(this, TeamStatsActivity::class.java))
        }

        //testingDataBase()

    }

    private fun testingDataBase() {

        val database = Firebase.database
        val playerRef = database.getReference("Players")
        val teamRef = database.getReference("Teams")

        val newTeam = TeamData("","", "Hangover 69")

        val teamID = teamRef.push().key

        if(teamID == null) {
            Log.w("Sven", "Could not get push key for new player")
            return
        }

        val member1 = playerRef.orderByChild("name").equalTo("Till Martini")
        val member2 = playerRef.orderByChild("name").equalTo("Felix Graeber")

        updatePlayerTeamID(member1, teamID)
        updatePlayerTeamID(member2, teamID)

        teamRef.child(teamID).setValue(newTeam)

        addPlayersToTeam(member1, member2, teamID, teamRef)

    }

    fun addPlayersToTeam(player1Query: Query,player2Query: Query, teamID: String, teamRef: DatabaseReference) {
        player1Query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value as HashMap<String, String>
                val entry = value.entries.iterator().next()
                val key = entry.key
                teamRef.child(teamID).child("member1ID").setValue(key)
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
        player2Query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value as HashMap<String, String>
                val entry = value.entries.iterator().next()
                val key = entry.key
                teamRef.child(teamID).child("member2ID").setValue(key)
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    fun updatePlayerTeamID(playerQuery: Query, teamID: String) {

        playerQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value as HashMap<String, String>
                val entry = value.entries.iterator().next()
                val key = entry.key
                val playerRef = playerQuery.ref
                playerRef.child(key).child("teamID").setValue(teamID)
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }
}