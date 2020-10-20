package com.example.flunkystats

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.flunkystats.database.DataBaseHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


abstract class StatsActivity: AppCompatActivity() {

    lateinit var dbHelper: DataBaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DataBaseHelper(this)
    }


    /**
     * Loads the Name of the Player with ID [playerID]
     * and gives the name + [targetView] to the callback function
     */
    protected fun loadPlayerName(playerID: String, targetView: View) {

    }


    /**
     * Loads the Name of the Team with ID [teamID]
     * and gives the name + [targetView] to the callback function
     */
    protected fun loadTeamName(teamID: String, targetView: View) {

    }



    /**
     * Adds all Names from [nameList] to the [targetView] as new TextViews.
     * [textSize] defines the font size of the new TextViews. default is 36dp
     */
    protected fun createTextViews(nameList: ArrayList<String> , targetView: View, textSize: Float = 36f) {
    }

    /**
     * Adds a TextView to [targetView] with [name] as Text and font size [textSize]
     * default [textSize] is 36dp
     * the ID of the new TextView will be [name].hashCode()
     * Returns the new TextView
     */
    protected fun createTextView(name: String, entryID :String, targetView: View, textSize: Float = 36f): TextView {
        val newTV:TextView = TextView.inflate(this, R.layout.inflatable_stats_text_view, null) as TextView
        newTV.text = name
        newTV.tag = entryID
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

    }

    /**
     * loads the total number of tournaments and wins for player with [playerID]
     * and puts the into [gamesView] and [winsView]
     */
    protected fun loadPlayerTournNumbStats(playerID: String, gamesView: TextView, winsView: TextView) {

    }
}