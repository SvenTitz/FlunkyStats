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


    /**
     * Loads the Name of the Player with ID [playerID] and adds it to [targetView]
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

    abstract fun loadPlayerNameCallback(name: String, targetView: View)


    /**
     * Loads the Name of Team with ID [teamID] and adds it to [targetView] if all other Team Names are done loading
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

    abstract fun loadTeamNameCallback(teamName: String?, targetView: View)

    /**
     * Adds all Names from [nameList] to the [targetView] as text views.
     */
    protected fun createTextViews(nameList: ArrayList<String> , targetView: View) {
        createTextViews(nameList, targetView, 36F)
    }

    protected fun createTextViews(nameList: ArrayList<String> , targetView: View, textSize: Float) {
        nameList.forEach { name ->
            createTextView(name, targetView, textSize)
        }
    }

    /**
     * Adds a TextView to [targetView] with [name] as Text
     */
    protected fun createTextView(name: String, targetView: View, textSize: Float): TextView {
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
     * loads the hit ratio and average slugs of player with [playerID].
     * Hit ratio is calculated from ratio between sum of all hits divided by all shots
     * Average slugs is calculated by sum of all Slugs in winning games, divided by the number of winning games
     * Writes the values to [targetViews]: [0] for hit ratio and [1] for avg. slugs
     */
    protected fun loadPlayerMatchStats(playerID: String, targetViews: List<View>) {
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
                //loop through all matches and add up shots and hits
                var sumShots = 0f
                var sumHits = 0f
                var sumSlugs = 0f
                var numbWinningGames = 0f
                values.forEach { (_, v) ->
                    sumShots += v["shots"]?.toFloat() ?: 0f
                    sumHits += v["hits"]?.toFloat() ?: 0f
                    if (v["won"] == "TRUE") {
                        numbWinningGames++
                        sumSlugs += v["slugs"]?.toFloat() ?: 0f
                    }
                }
                val hitRatioF = sumHits / sumShots * 100
                val hitRatioS = String.format("%.2f", hitRatioF) + "%"
                val avgSlugs = sumSlugs / numbWinningGames
                (targetViews[0] as TextView).text = hitRatioS
                (targetViews[1] as TextView).text = avgSlugs.toString()
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }
}