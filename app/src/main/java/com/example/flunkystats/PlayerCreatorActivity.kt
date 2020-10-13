package com.example.flunkystats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.player_creator.*

class PlayerCreatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_creator)

        btnAddPlayer.setOnClickListener {
            //if $etName.value is empty throw error
            //Add New Player with name $etName.value to database
            //if $etTeamName is not empty, try to find team in database and add this player to it
            finish() //go back one screen to player list
        }
    }

}