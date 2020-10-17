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
     * Loads the Name of Team with ID [teamID] and adds it if all other Team Names are done loading
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
}