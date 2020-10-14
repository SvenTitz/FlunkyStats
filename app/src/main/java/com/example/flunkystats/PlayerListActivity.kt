package com.example.flunkystats

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_player_list.*

class PlayerListActivity: AppCompatActivity(), LoadsData {

    private val database = Firebase.database
    private val playerRef = database.reference.child("Players")

    companion object {
        var btnIDs = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_list)

        loadPlayers()

        fabAddPlayer.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
            builder.setTitle("Spieler Hinzufügen:")

            val edittext:EditText = EditText.inflate(this, R.layout.inflatable_edit_text, null) as EditText
            edittext.hint = "Spieler Name"
            builder.setView(edittext)

            builder.setPositiveButton("Hinzufügen",
                DialogInterface.OnClickListener { dialog, id ->
                    addPlayer(edittext.text.toString())
                    dialog.cancel()
                })

            builder.setNegativeButton("Abbrechen",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })

            val dialog :AlertDialog = builder.show()
        }
    }

    private fun createNewButton(playerName: String, playerID: String, targetLayout: ViewGroup):Button {
        var newBtn = Button(this)
        //set tag
        newBtn.tag = playerID
        //set Text
        newBtn.text = playerName;
        //set Background
        newBtn.setBackgroundResource(R.drawable.btn_primary_color)
        //add click listener
        newBtn.setOnClickListener {
            Log.d("Sven", "button tag: ${newBtn.tag.toString()}")
            val intent = Intent(this, PlayerStatsActivity::class.java).apply {
                putExtra(AppConfig.EXTRA_MESSAGE_PLAYER_ID, newBtn.tag.toString())
            }
            startActivity(intent)
        }
        //add button to view group
        targetLayout.addView(newBtn)
        //add margins
        var param = newBtn.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(10,10,10,10)
        newBtn.layoutParams = param

        return newBtn
    }

    private fun loadSinglePlayer(playerID: String) {
        val playerQ = playerRef.orderByKey().equalTo(playerID)
        playerQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                val entry = values.iterator().next().value
                val name = entry.iterator().next().value
                createNewButton(name, playerID, llPlayerList)

            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    private fun loadPlayers() {

        var pgsBar = addProgressBar(findViewById(R.id.plaConstLayout), this)
        pgsBar.scaleX = 2F
        pgsBar.scaleY = pgsBar.scaleX
        pgsBar.visibility = View.VISIBLE

         playerRef.addListenerForSingleValueEvent(object : ValueEventListener {
             @RequiresApi(Build.VERSION_CODES.N)
             override fun onDataChange(dataSnapshot: DataSnapshot) {
                 val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                 Log.d("Sven", "values: $values")
                 var players = mutableMapOf<String, String>()
                 values.forEach { (k, v) ->
                     Log.d("Sven", "Entry: Key $k, Value: $v")
                     val name = v.iterator().next().value
                     players[k] = name
                     //createNewButton(name, k, llPlayerList)
                 }
                 pgsBar.visibility = View.GONE
                 players.forEach { (playerID, name ) ->
                     createNewButton(name, playerID, llPlayerList)
                 }

             }
             override fun onCancelled (error: DatabaseError) {
                 Log.w("Sven", "Failed to read value.", error.toException())
             }
         })


    }

    private fun addPlayer(name: String) {

        val newPlayerID = playerRef.push().key

        if(newPlayerID != null) {
            playerRef.child(newPlayerID).child("name").setValue(name)
            loadSinglePlayer(newPlayerID)
        }
        else {
            Log.w("Sven", "Failed to push new player")
        }
    }

}