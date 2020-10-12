package com.example.flunkystats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
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

        testingDataBase()

    }

    private fun testingDataBase() {

        val database = Firebase.database
        val playerRef = database.getReference("Players")

        val player = Player("Lukas Stachelscheid", "100002")

        playerRef.child("000003").setValue(player)


//        ike.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val value = dataSnapshot.getValue<String>()
//                Log.d("Sven", "Value is $value")
//            }
//            override fun onCancelled (error: DatabaseError) {
//                Log.w("Sven", "Failed to read value.", error.toException())
//            }
//
//        })

    }
}