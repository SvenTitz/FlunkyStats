package com.example.flunkystats

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_player_stats.*


class PlayerStatsActivity: AppCompatActivity(), LoadsData {

    //References to the database
    private val database = Firebase.database
    private val playerRef = database.getReference("Players")
    private val teamMembRef = database.getReference("TeamMembership")
    private val teamsRef = database.getReference("Teams")

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

        loadPlayerName(playerID)

        loadPlayerTeams(playerID)

        //TODO: Load Rest of Data

    }

    /**
     * Loads the Name of the Player with ID: [playerID]
     */
    private fun loadPlayerName(playerID: String) {

        val playerQuery = playerRef.orderByKey().equalTo(playerID)
        playerQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                val entry = values.iterator().next()
                tvPName.text = entry.value["name"]
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    /**
     * Loads all Teams for the player with ID: [playerID]
     */
    private fun loadPlayerTeams(playerID: String) {
        //add progress bar
        teamPgsBar.visibility = View.VISIBLE

        //search for team memberships of player with ID: [playerID]
        val teamMembQ = teamMembRef.orderByChild("memberID").equalTo(playerID)

        //read the teamID for each membership
        teamMembQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value == null) {
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
                    loadTeamName(teamID)
                }
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    /**
     * Loads the Name of Team with ID [teamID] and adds it if all other Team Names are done loading
     */
    private fun loadTeamName(teamID: String) {
        //loading one more team name
        countTeamsLoading++

        //query the team by $teamID and read values
        val teamQ = teamsRef.orderByKey().equalTo(teamID)
        teamQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                //get the entry of the team with id $teamID
                val entry = values.iterator().next()
                //save the name and add it to the teamNames ArrayList
                val teamName = entry.value["name"]
                if (teamName == null) {
                    Log.w("Sven", "Team name is null")
                    countTeamsLoading--  //done loading team name
                    return
                }
                teamNamesList.add(teamName)
                //done loading team name
                countTeamsLoading--
                //create the team text views if $countTeamsLoading is 0
                createTeamTextViews()
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    /**
     * IF Team Names are still loading: do nothing.
     * OTHERWISE: Adds all Team Names from [teamNamesList] to the Layout.
     */
    private fun createTeamTextViews() {
        if(countTeamsLoading != 0) {
            //there are still team names loading
            return
        }
        else {
            //all team names are done loading -> create the TextViews
            teamPgsBar.visibility = View.GONE
            teamNamesList.forEach { name ->
                createTeamTextView(name)
            }
        }
    }

    /**
     * Adds a TextView to [llPTeams] with [teamName] as Text
     */
    private fun createTeamTextView(teamName: String): TextView {
        val newTV:TextView = TextView.inflate(this, R.layout.inflatable_stats_text_view, null) as TextView
        newTV.text = teamName
        newTV.id = teamName.hashCode()

        llPTeams.addView(newTV)

        return newTV
    }
}