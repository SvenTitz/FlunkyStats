package com.example.flunkystats

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.flunkystats.util.StringUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_player_list.*

class PlayerListActivity: AppCompatActivity(), LoadsData {

    //references to database
    private val database = Firebase.database
    private val playerRef = database.reference.child("Players")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_list)

        //loads Player data and creates a button for each player
        loadPlayers() //TODO: save to/load from cache

        //set on click listener for floating action button "add Player"
        fabAddPlayer.setOnClickListener {
            //open the add player alert dialog
            openAddPlayerDialog()
        }
    }

    /**
     * creates a new button for the player of name [playerName] and ID [playerID] and adds it to layout [targetLayout]
     */
    private fun createNewPlayerButton(playerName: String, playerID: String, targetLayout: ViewGroup):Button {
        //create the new button
        val newBtn = Button(this) //TODO: change to inflatable
        newBtn.text = playerName
        newBtn.setBackgroundResource(R.drawable.btn_primary_color)
        //set payer ID as tag
        newBtn.tag = playerID

        //add click listener
        newBtn.setOnClickListener {
            //Open stats page of the player. send player ID as extra message
            val intent = Intent(this, PlayerStatsActivity::class.java).apply {
                putExtra(AppConfig.EXTRA_MESSAGE_PLAYER_ID, newBtn.tag.toString())
            }
            startActivity(intent)
        }
        //add button to view group
        targetLayout.addView(newBtn)
        //add margins
        val param = newBtn.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(10,10,10,10)
        newBtn.layoutParams = param

        return newBtn
    }

    /**
     * loads a single player with ID [playerID] from the databse.
     * This method is called after a new player is added with the floating action button
     */
    private fun loadSinglePlayer(playerID: String) {
        //search for the player
        val playerQ = playerRef.orderByKey().equalTo(playerID)
        //get the player name from the database
        playerQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                val entry = values.iterator().next().value
                val name = entry.iterator().next().value
                //add a button for the player to the player list
                createNewPlayerButton(name, playerID, llPlayerList)
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    /**
     * loads all players when the activity is created
     */
    private fun loadPlayers() {

        //add progress bar while players are loading
        val pgsBar = addProgressBar(findViewById(R.id.plaConstLayout), this)
        pgsBar.scaleX = 2F
        pgsBar.scaleY = pgsBar.scaleX
        pgsBar.visibility = View.VISIBLE

        //load all player data
        playerRef.orderByChild("name").addListenerForSingleValueEvent(object : ValueEventListener {
             override fun onDataChange(dataSnapshot: DataSnapshot) {
                 @Suppress("UNCHECKED_CAST")
                 val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>

                 //create map of player IDs and names
                 val players = mutableMapOf<String, String>()

                 //loop through all player and add names and ids to map
                 values.forEach { (k, v) ->
                     val name = v.iterator().next().value
                     players[k] = name
                 }

                 //after all data has been loaded, add the buttons to the list
                 pgsBar.visibility = View.GONE
                 players.forEach { (playerID, name ) ->
                     createNewPlayerButton(name, playerID, llPlayerList)
                 }

             }

             override fun onCancelled (error: DatabaseError) {
                 Log.w("Sven", "Failed to read value.", error.toException())
             }
         })


    }

    /**
     * Opens the Add Player Alert Dialog
     */
    private fun openAddPlayerDialog() {
        //create new alert dialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
        builder.setTitle("Spieler Hinzufügen:")

        //add the EditText for the Player Name to the Dialog
        val edittext:EditText = EditText.inflate(this, R.layout.inflatable_edit_text, null) as EditText
        edittext.hint = "Spieler Name"
        builder.setView(edittext)

        //add functionality to positive Button
        builder.setPositiveButton("Hinzufügen") { dialog, _ ->
            //add a new player to database
            val playerName:String = StringUtil.capitalizeFirstLetters(edittext.text.toString())
            addPlayer(playerName, true)
            dialog.cancel()
        }

        //add functionality to negative Button
        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            dialog.cancel()
        }

        //open the dialog
        builder.show()
    }

    /**
     * Adds a new player with name: [name] to the database
     */
    private fun addPlayer(name: String) {
        //creates new entry with random key
        val newPlayerID = playerRef.push().key

        if(newPlayerID != null) {
            //add the player to the database
            playerRef.child(newPlayerID).child("name").setValue(name)
            loadSinglePlayer(newPlayerID)
        }
        else {
            Log.w("Sven", "Failed to push new player")
        }
    }

    /**
     * Adds a new palyer with name [name] to the database
     * if [checkDupName] checks for duplicate names first
     */
    @Suppress ("SameParameterValue")
    private fun addPlayer(name: String, checkDupName :Boolean) {

        //add player without checking for duplicate names
        if( !checkDupName ) {
            addPlayer(name)
            return
        }

        //search for players with [name]
        val playerQ = playerRef.orderByChild("name").equalTo(name)
        playerQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.value == null) {
                    //no player with this name found -> add player
                    addPlayer(name)
                }
                else {
                    //player with duplicate name found -> open dialog and ask to add anyway
                    openDupNameDialog(name)
                }

            }

            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })

    }

    /**
     * used when trying to add a player with duplicate [name] to the database
     * give option to add player anyway, or cancel
     */
    private fun openDupNameDialog(name: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
        builder.setTitle("Spieler mit diesem Namen Existiert bereits.")
        builder.setMessage("Trotzdem hinzufügen?")

        //add functionality to positive Button
        builder.setPositiveButton("Hinzufügen") { dialog, _ ->
            addPlayer(name)
            dialog.cancel()
        }

        //add functionality to negative Button
        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

}