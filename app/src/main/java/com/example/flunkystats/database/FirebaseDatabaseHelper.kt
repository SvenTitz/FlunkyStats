package com.example.flunkystats.database

import android.util.Log
import com.example.flunkystats.database.models.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class FirebaseDatabaseHelper(private val dbHelper: DataBaseHelper) {

    companion object {
        const val PLAYERS = "Players"
        const val TEAMS = "Teams"
        const val MATCHES = "Matches"
        const val TOURNAMENTS = "Tournaments"
        const val PLAYER_TEAM_PAIRS = "PlayerTeamPairs"
        const val MATCH_PLAYER_PAIRS = "MatchPlayerPairs"
        const val MATCH_TEAM_PAIRS = "MatchTeamPairs"
        const val TIMESTAMPS = "Timestamps"

        const val PLAYER_ID = "playerID"
        const val TEAM_ID = "teamID"
        const val MATCH_ID = "matchID"
        const val TOURNAMENT_ID = "tournID"
        const val NAME = "name"
        const val HITS = "hits"
        const val SHOTS = "shots"
        const val SLUGS = "slugs"
        const val WON = "won"
        const val TOURNAMENT_TYPE = "type"
        const val NUMBER_OF_TEAMS = "numbTeams"
        const val WINNER_ID = "winnerID"

        const val TRUE = "true"
        const val FALSE = "false"
    }

    private val database = Firebase.database
    private val playersRef = database.getReference(PLAYERS)
    private val teamsRef = database.getReference(TEAMS)
    private val matchesRef = database.getReference(MATCHES)
    private val tournsRef = database.getReference(TOURNAMENTS)
    private val playerTeamPairsRef = database.getReference(PLAYER_TEAM_PAIRS)
    private val matchPlayerPairsRef = database.getReference(MATCH_PLAYER_PAIRS)
    private val matchTeamRef = database.getReference(MATCH_TEAM_PAIRS)
    private val timestampsRef = database.getReference(TIMESTAMPS)



    fun reloadEntireDatabase() {
        reloadPlayers()
        reloadTeams()
        reloadMatches()
        reloadTournaments()
        reloadPlayerTeamPairs()
        reloadMatchPlayerPairs()
        reloadMatchTeamPairs()
        reloadTimestamps()
    }

    fun reloadPlayers() {
        dbHelper.clearTable(DataBaseHelper.TABLE_PLAYERS)

        playersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                //loop through all entries and add them to the database
                values.forEach { (k, v) ->
                    val player = PlayerModel(k, v[NAME])
                    dbHelper.addPlayer(player)
                }
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    fun reloadTeams() {
        dbHelper.clearTable(DataBaseHelper.TABLE_TEAMS)

        teamsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                //loop through all entries and add them to the database
                values.forEach { (k, v) ->
                    val team = TeamModel(k, v[NAME])
                    dbHelper.addTeam(team)
                }
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    fun reloadMatches() {
        dbHelper.clearTable(DataBaseHelper.TABLE_MATCHES)

        matchesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                //loop through all entries and add them to the database
                values.forEach { (k, v) ->
                    val match = MatchModel(k, v[TOURNAMENT_ID])
                    dbHelper.addMatch(match)
                }
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    fun reloadTournaments() {
        dbHelper.clearTable(DataBaseHelper.TABLE_TOURNAMENTS)

        tournsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, *>>
                //loop through all entries and add them to the database
                values.forEach { (k, v) ->
                    val winnerID = v[WINNER_ID] as String
                    val name = v[NAME] as String
                    val numbTeams = (v[NUMBER_OF_TEAMS] as Long).toInt()
                    val tournType = v[TOURNAMENT_TYPE] as String
                    val tourn = TournamentModel(k, winnerID, name, numbTeams, tournType)
                    dbHelper.addTournament(tourn)
                }
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    fun reloadPlayerTeamPairs() {
        dbHelper.clearTable(DataBaseHelper.TABLE_PLAYER_TEAM_PAIR)

        playerTeamPairsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                //loop through all entries and add them to the database
                values.forEach { (k, v) ->
                    val pair = PlayerTeamPairModel(k, v[PLAYER_ID], v[TEAM_ID])
                    dbHelper.addPlayerTeamPair(pair)
                }
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    fun reloadMatchPlayerPairs() {
        dbHelper.clearTable(DataBaseHelper.TABLE_MATCH_PLAYER_PAIR)

        matchPlayerPairsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, *>>
                //loop through all entries and add them to the database
                values.forEach { (k, v) ->
                    val matchID = v[MATCH_ID] as String
                    val playerID = v[PLAYER_ID] as String
                    val shots = (v[SHOTS] as Long).toInt()
                    val hits = (v[HITS] as Long).toInt()
                    val slugs = (v[SLUGS] as Long).toInt()
                    val won = v[WON] as Boolean
                    val pair = MatchPlayerPairModel(k, matchID, playerID, shots, hits, slugs, won)
                    dbHelper.addMatchPlayerPair(pair)
                }
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    fun reloadMatchTeamPairs() {
        dbHelper.clearTable(DataBaseHelper.TABLE_MATCH_TEAM_PAIR)

        matchTeamRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, *>>
                //loop through all entries and add them to the database
                values.forEach { (k, v) ->
                    val matchID = v[MATCH_ID] as String
                    val teamID = v[TEAM_ID] as String
                    val won = v[WON] as Boolean
                    val pair = MatchTeamPairModel(k, matchID, teamID, won)
                    dbHelper.addMatchTeamPair(pair)
                }
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    fun reloadTimestamps() {
        dbHelper.clearTable(DataBaseHelper.TABLE_TIMESTAMPS)

        timestampsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, Long>
                //loop through all entries and add them to the database
                val timestamp = TimestampsModel(
                    values[PLAYERS],
                    values[TEAMS],
                    values[MATCHES],
                    values[TOURNAMENTS],
                    values[PLAYER_TEAM_PAIRS],
                    values[MATCH_PLAYER_PAIRS],
                    values[MATCH_TEAM_PAIRS]
                )
                dbHelper.addTimestamps(timestamp)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    fun checkUpToDate(callbackFun: (res: Boolean) -> Unit) {
        val dbTimestamps = dbHelper.getTimestamps()
        if(dbTimestamps == null) {
            callbackFun(false)
            return
        }

            timestampsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, Long>
                //loop through all entries and add them to the database
                val fbTimestamps = ArrayList<Int>()
                fbTimestamps.add(values[PLAYERS]?.toInt() ?: 0)
                fbTimestamps.add(values[TEAMS] ?.toInt() ?: 0)
                fbTimestamps.add(values[MATCHES]?.toInt() ?: 0)
                fbTimestamps.add(values[TOURNAMENTS]?.toInt() ?: 0)
                fbTimestamps.add(values[PLAYER_TEAM_PAIRS]?.toInt() ?: 0)
                fbTimestamps.add(values[MATCH_PLAYER_PAIRS]?.toInt() ?: 0)
                fbTimestamps.add(values[MATCH_TEAM_PAIRS]?.toInt() ?: 0)

                for(i in 0 until fbTimestamps.size) {
                    if(dbTimestamps[i] < fbTimestamps[i]) {
                        callbackFun(false)
                        return
                    }
                }
                callbackFun(true)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }
}