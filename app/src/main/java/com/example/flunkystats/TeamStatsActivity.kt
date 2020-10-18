package com.example.flunkystats

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.example.flunkystats.util.StringUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class TeamStatsActivity: StatsActivity() {

    private lateinit var memberPgsBar: ProgressBar
    private var countPlayersLoading = 0
    private var playerNamesList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_stats)


        memberPgsBar = addProgressBar(findViewById<LinearLayout>(R.id.llTPlayers), this)

        val teamID = intent.getStringExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID)

        if(teamID == null) {
            //TODO: throw proper error
            Log.w("Sven", "team ID could not be transfered")
            return
        }

        val idText = "ID: $teamID"
        findViewById<TextView>(R.id.tvTID).text = idText

        loadTeamName(teamID, findViewById(R.id.tvTName))

        loadTeamPlayers(teamID, findViewById<LinearLayout>(R.id.llTPlayers))

        val teamStatsTVs = arrayOf<TextView>(
            findViewById(R.id.tvTHits),
            findViewById(R.id.tvTSlugs),
            findViewById(R.id.tvTGamesTotal),
            findViewById(R.id.tvTGamesWon),
            findViewById(R.id.tvTGamesWonRatio)
        )
        loadTeamMatchStats(teamID, *teamStatsTVs)

        loadTeamTournNumbStats(teamID,
            findViewById(R.id.tvTTurnamentsTotal),
            findViewById(R.id.tvTTurnamentsWon))
    }


    override fun loadTeamNameCallback(teamName: String?, targetView: View) {
        (targetView as TextView).text = teamName ?: "Error loading team name"
    }


    private fun loadTeamPlayers(teamID: String, targetLayout: LinearLayout) {
        //add progress bar
        memberPgsBar.visibility = View.VISIBLE

        val teamMembQ = teamMembRef.orderByChild("teamID").equalTo(teamID)

        //read the teamID for each membership
        teamMembQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value == null) {
                    //no teams found
                    Log.w("Sven", "dataSnapshot was null")
                    return
                }
                //loop through all players found
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>

                var count = 1
                values.forEach { (_, v) ->
                    //read team name and return if null
                    val playerID = v["playerID"] ?: return
                    //read the team name and add it to the teamNames array list
                    countPlayersLoading++
                    loadPlayerName(playerID, targetLayout)

                    //TODO: das hier ist hässlich
                    val playerTVs =
                        if (count == 1) {
                            arrayOf<TextView>(
                                findViewById(R.id.tvTHits1),
                                findViewById(R.id.tvTSlugs1)
                            )
                        } else {
                            arrayOf<TextView>(
                                findViewById(R.id.tvTHits2),
                                findViewById(R.id.tvTSlugs2)
                            )
                        }

                    loadPlayerMatchStats(playerID, *playerTVs)
                    count++
                }

            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }


    override fun loadPlayerNameCallback(playerName: String, targetView: View) {
        //done loading the player name
        countPlayersLoading--

        playerNamesList.add(StringUtil.newLineEachWord(playerName))
            //create the team text views if [countTeamsLoading] is 0
        if (countPlayersLoading == 0) {
            memberPgsBar.visibility = View.GONE
            createTextViews(playerNamesList, targetView, 24F)
        }
    }


    /**
     * loads the hit ratio, average slugs and number of Games/wins of team with [teamID].
     * Hit ratio is calculated from ratio between sum of all hits divided by all shots.
     * Average slugs is calculated by sum of all Slugs in winning games, divided by the number of winning games.
     * Number of Games is displayed alongside number of Wins and ratio between win/games
     * [targetViews] is the list of views that should be updated
     */
    private fun loadTeamMatchStats(teamID: String, vararg targetViews: TextView) {
        //TODO: zusammenführen mit loadplayermatchstats?
        val teamMatchesQ = matchTeamRef.orderByChild("teamID").equalTo(teamID)

        teamMatchesQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //no teams found
                    Log.w("Sven", "dataSnapshot was null")
                    return
                }
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                //loop through all matches calculate stats
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
                        R.id.tvTHits ->
                            it.text = hitRatioS
                        R.id.tvTSlugs ->
                            it.text = avgSlugs
                        R.id.tvTGamesTotal ->
                            it.text = sumGames.toString()
                        R.id.tvTGamesWon ->
                            it.text = sumWins.toInt().toString()
                        R.id.tvTGamesWonRatio ->
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
     * loads the total number of tournaments and wins for team with [teamID]
     * and puts the into [gamesView] and [winsView]
     */
    private fun loadTeamTournNumbStats(teamID: String, gamesView: TextView, winsView: TextView) {
        val teamTournQ = tournTeamRef.orderByChild("teamID").equalTo(teamID)

        teamTournQ.addListenerForSingleValueEvent(object : ValueEventListener {
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