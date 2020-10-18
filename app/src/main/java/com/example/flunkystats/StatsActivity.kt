package com.example.flunkystats

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


abstract class StatsActivity: AppCompatActivity(), LoadsData {

    //References to the database

    protected val database = Firebase.database
    protected val playerRef = database.getReference("Players")
    protected val teamMembRef = database.getReference("TeamMembership")
    protected val teamsRef = database.getReference("Teams")
    protected val matchPlayerRef = database.getReference("MatchPlayerPairs")
    protected val tournPlayerRef = database.getReference("TournamentPlayerPairs")
    protected val matchTeamRef = database.getReference("MatchTeamPairs")
    protected val tournTeamRef = database.getReference("TournamentTeamPairs")


    /**
     * Loads the Name of the Player with ID [playerID]
     * and gives the name + [targetView] to the callback function
     */
    protected fun loadPlayerName(playerID: String, targetView: View) {

        val playerQuery = playerRef.orderByKey().equalTo(playerID)
        playerQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                val entry = values.iterator().next()
                val name = entry.value["name"] ?: return
                loadPlayerNameCallback(name, targetView)
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    /**
     * function called upon retrieving a new player [playerName]
     * [targetView] is the view relevant for the player name
     */
    abstract fun loadPlayerNameCallback(playerName: String, targetView: View)


    /**
     * Loads the Name of the Team with ID [teamID]
     * and gives the name + [targetView] to the callback function
     */
    protected fun loadTeamName(teamID: String, targetView: View) {

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
                loadTeamNameCallback(teamName, targetView)
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    /**
     * function called upon retrieving a new [teamName]
     * [targetView] is the view relevant for the player name
     */
    abstract fun loadTeamNameCallback(teamName: String?, targetView: View)


    /**
     * Adds all Names from [nameList] to the [targetView] as new TextViews.
     * [textSize] defines the font size of the new TextViews. default is 36dp
     */
    protected fun createTextViews(nameList: ArrayList<String> , targetView: View, textSize: Float = 36f) {
        nameList.forEach { name ->
            createTextView(name, targetView, textSize)
        }
    }

    /**
     * Adds a TextView to [targetView] with [name] as Text and font size [textSize]
     * default [textSize] is 36dp
     * the ID of the new TextView will be [name].hashCode()
     * Returns the new TextView
     */
    protected fun createTextView(name: String, targetView: View, textSize: Float = 36f): TextView {
        val newTV:TextView = TextView.inflate(this, R.layout.inflatable_stats_text_view, null) as TextView
        newTV.text = name
        newTV.id = name.hashCode()
        newTV.textSize = textSize
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1F)
        newTV.layoutParams = params

        (targetView as ViewGroup).addView(newTV)

        return newTV
    }


    /**
     * loads the hit ratio, average slugs and number of Games/wins of player with [playerID].
     * Hit ratio is calculated from ratio between sum of all hits divided by all shots.
     * Average slugs is calculated by sum of all Slugs in winning games, divided by the number of winning games.
     * Number of Games is displayed alongside number of Wins and ratio between win/games
     * [targetViews] is the list of views that should be updated
     */
    protected fun loadPlayerMatchStats(playerID: String, vararg targetViews: TextView) {
        //TODO: zusammenführen mit loadteammatchstats?
        val playerMatchesQ = matchPlayerRef.orderByChild("playerID").equalTo(playerID)

        playerMatchesQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //no teams found
                    Log.w("Sven", "dataSnapshot was null")
                    return
                }
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                //loop through all matches calculate the stats
                var sumShots = 0f
                var sumHits = 0f
                var sumSlugs = 0f
                var sumWins = 0f
                val sumGames = values.size
                values.forEach { (_, v) ->
                    sumShots += v["shots"]?.toFloat() ?: 0f
                    sumHits += v["hits"]?.toFloat() ?: 0f
                    if (v["won"] == "TRUE") {
                        sumWins++
                        sumSlugs += v["slugs"]?.toFloat() ?: 0f
                    }
                }
                val hitRatioF = sumHits / sumShots * 100
                val hitRatioS = String.format("%.1f", hitRatioF) + "%"
                val avgSlugs = String.format("%.1f", sumSlugs / sumWins)
                val winRatioF = sumWins / sumGames * 100
                val winRatioS = String.format("%.0f", winRatioF) + "%"

                //apply stats to corresponding views
                targetViews.forEach {
                    when (it.id) {
                        R.id.tvTHits1, R.id.tvTHits2, R.id.tvPHits ->
                            it.text = hitRatioS
                        R.id.tvTSlugs1, R.id.tvTSlugs2, R.id.tvPSlugs ->
                            it.text = avgSlugs
                        R.id.tvPGamesTotal ->
                            it.text = sumGames.toString()
                        R.id.tvPGamesWon ->
                            it.text = sumWins.toInt().toString()
                        R.id.tvPGamesWonRatio ->
                            it.text = winRatioS
                    }
                }
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    /**
     * loads the total number of tournaments and wins for player with [playerID]
     * and puts the into [gamesView] and [winsView]
     */
    protected fun loadPlayerTournNumbStats(playerID: String, gamesView: TextView, winsView: TextView) {
        val playerTournQ = tournPlayerRef.orderByChild("playerID").equalTo(playerID)

        playerTournQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>

                val sumTourn = values.size
                var sumWins = 0
                values.forEach { (_, v) ->
                    if(v["won"] != null && v["won"] == "TRUE")  {
                        sumWins++
                    }
                }
                gamesView.text = sumTourn.toString()
                winsView.text = sumWins.toString()
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }
}