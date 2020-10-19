package com.example.flunkystats

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_player_list.*

class PlayerListActivity: ListActivity() {

    override val targetStatsActivity: Class<*>
        get() = PlayerStatsActivity::class.java
    override val dataRef: DatabaseReference
        get() = Firebase.database.reference.child("Players")
    override val targetButtonLayout: ViewGroup
        get() = llPlayerList
    override val rootLayout: ConstraintLayout
        get() = findViewById<ConstraintLayout>(R.id.plaConstLayout)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_list)

        //loads Player data and creates a button for each player
        loadEntries()

        //set on click listener for floating action button "add Player"
        fabAddPlayer.setOnClickListener {
            //open the add player alert dialog
            Toast.makeText(this, "disabled for now", Toast.LENGTH_LONG).show()
            //openAddEntryDialog(getString(R.string.addPlayerDialogTitle), getString(R.string.addPlayerDialogHint))
        }
    }

}