package com.example.flunkystats.database

import android.util.Log
import com.example.flunkystats.models.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.reflect.KFunction0
import com.example.flunkystats.AppConfig.Companion.TAG
import com.google.firebase.database.*

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
        const val MATCH_NUMB = "matchNumb"

        const val AUTH_TEST = "authTest"

        private var currentlyUpdating: Int = 0
    }

    private val database = Firebase.database
    private val playersRef = database.getReference(PLAYERS)
    private val teamsRef = database.getReference(TEAMS)
    private val matchesRef = database.getReference(MATCHES)
    private val tournsRef = database.getReference(TOURNAMENTS)
    private val playerTeamPairsRef = database.getReference(PLAYER_TEAM_PAIRS)
    private val matchPlayerPairsRef = database.getReference(MATCH_PLAYER_PAIRS)
    private val matchTeamPairsRef = database.getReference(MATCH_TEAM_PAIRS)
    private val timestampsRef = database.getReference(TIMESTAMPS)





    fun updateDatabase(doneUpdating: (Boolean) -> Unit) {

        fun update() {
            Log.d(TAG, "update called with currentlyUpdating at:${currentlyUpdating}")
            currentlyUpdating--
            if (currentlyUpdating <= 0) {
                doneUpdating(true)
            }
        }

        checkUpToDate { upToDate ->
            currentlyUpdating = upToDate.count { !it } +1 //+1 for timestamps or call allDone right away
            Log.d(TAG, "currentlyUpdating set to ${currentlyUpdating}")
            if (!upToDate[0]) reloadPlayers(::update)
            if (!upToDate[1]) reloadTeams(::update)
            if (!upToDate[2]) reloadMatches(::update)
            if (!upToDate[3]) reloadTournaments(::update)
            if (!upToDate[4]) reloadPlayerTeamPairs(::update)
            if (!upToDate[5]) reloadMatchPlayerPairs(::update)
            if (!upToDate[6]) reloadMatchTeamPairs(::update)
            if (upToDate.any{!it}) {
                Log.d(TAG, "any false")
                reloadTimestamps(::update)
            } else {
                Log.d(TAG, "no false")
                update()
            }
        }
    }

    private fun reloadPlayers(callbackFun: KFunction0<Unit>) {
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
                callbackFun()
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun reloadTeams(callbackFun: KFunction0<Unit>) {
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
                callbackFun()
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun reloadMatches(callbackFun: KFunction0<Unit>) {
        dbHelper.clearTable(DataBaseHelper.TABLE_MATCHES)

        matchesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                //loop through all entries and add them to the database
                values.forEach { (k, v) ->
                    val match = MatchModel(k, v[TOURNAMENT_ID], v[MATCH_NUMB]!!.toInt())
                    dbHelper.addMatch(match)
                }
                callbackFun()
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun reloadTournaments(callbackFun: KFunction0<Unit>) {
        dbHelper.clearTable(DataBaseHelper.TABLE_TOURNAMENTS)

        tournsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, *>>
                //loop through all entries and add them to the database
                values.forEach { (k, v) ->
                    val winnerID = v[WINNER_ID] as? String
                    val name = v[NAME] as String
                    val numbTeams = (v[NUMBER_OF_TEAMS] as Long).toInt()
                    val tournType = v[TOURNAMENT_TYPE] as String
                    val tourn = TournamentModel(k, winnerID, name, numbTeams, tournType)
                    dbHelper.addTournament(tourn)
                }
                callbackFun()
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun reloadPlayerTeamPairs(callbackFun: KFunction0<Unit>) {
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
                callbackFun()
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun reloadMatchPlayerPairs(callbackFun: KFunction0<Unit>) {
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
                callbackFun()
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun reloadMatchTeamPairs(callbackFun: KFunction0<Unit>) {
        dbHelper.clearTable(DataBaseHelper.TABLE_MATCH_TEAM_PAIR)

        matchTeamPairsRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
                callbackFun()
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun reloadTimestamps(callbackFun: KFunction0<Unit>) {
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
                callbackFun()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }


    private fun checkUpToDate(callbackFun: (res: BooleanArray) -> Unit) {

        Log.d(TAG, "Checking if Database is UpToDate")
        val res = BooleanArray(7){false}

        val dbTimestamps = dbHelper.getTimestamps()
        if(dbTimestamps == null) {
            callbackFun(res)
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
                    val b = dbTimestamps[i] >= fbTimestamps[i]
                    res[i] =  b
                }
                callbackFun(res)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    fun testAuth(callbackFun: (Boolean) -> Unit) {
        val db = database.getReference(AUTH_TEST)
        val task = db.setValue("test")
        task.addOnCompleteListener {
            Log.d(TAG, "auth test complete with result: ${task.isSuccessful}")
            callbackFun(task.isSuccessful)
        }
    }

    fun addPlayer(playerName: String, callbackFun: (String) -> Unit) {
        val values = hashMapOf<String, Any>(NAME to playerName)
        addEntry(values, PLAYERS, callbackFun)
    }

    fun addTeam(teamName: String, callbackFun: (String) -> Unit) {
        val values = hashMapOf<String, Any>(NAME to teamName)
        addEntry(values, TEAMS, callbackFun)
    }

    fun addPlayerTeamPair(teamID: String, playerID: String, callbackFun: (String) -> Unit) {
        val values = hashMapOf<String, Any>(
            PLAYER_ID to playerID,
            TEAM_ID to teamID
        )
        addEntry(values, PLAYER_TEAM_PAIRS, callbackFun)
    }

    fun addMatch(tournID: String, matchNumb: Int, callbackFun: (String) -> Unit) {
        val values = hashMapOf<String, Any>(
            MATCH_NUMB to matchNumb.toString(),
            TOURNAMENT_ID to tournID
        )
        addEntry(values, MATCHES, callbackFun)
    }

    fun addMatchTeamPair(matchID: String, teamID: String, won: Boolean, callbackFun: ((String) -> Unit)?) {
        val values = hashMapOf<String, Any>(
            MATCH_ID to matchID,
            TEAM_ID to teamID,
            WON to won
        )
        addEntry(values, MATCH_TEAM_PAIRS, callbackFun)
    }

    fun addMatchPlayerPair(matchID: String, playerID: String, shots: Int, hits: Int, slugs: Int, won: Boolean, callbackFun: ((String) -> Unit)?) {
        val values = hashMapOf<String, Any>(
            MATCH_ID to matchID,
            PLAYER_ID to playerID,
            SHOTS to shots,
            HITS to hits,
            SLUGS to slugs,
            WON to won
        )
        addEntry(values, MATCH_PLAYER_PAIRS, callbackFun)
    }

    fun addEntry(entryValues: HashMap<String,Any>, table: String, callbackFun: ((String) -> Unit)?) {
        val ref = database.getReference(table)
        val entryID = ref.push().key
        if (entryID == null) {
            callbackFun?.invoke("")
            return
        }
        Log.d(TAG, entryID)
        val task = ref.child(entryID).setValue(entryValues)
        task.addOnCompleteListener {
            Log.d(TAG, "add entry complete with result: ${task.isSuccessful}")
            updateTimestamp(table)
            if(task.isSuccessful) {
                callbackFun?.invoke(entryID)
            }
            else {
                callbackFun?.invoke("")
            }
        }
    }

    private fun updateTimestamp(table: String) {
        timestampsRef.child(table).setValue(System.currentTimeMillis()/1000)
    }

    private fun deleteNestedEntry(childName: String, childValue:String , tableRef: DatabaseReference) {
        val q: Query = tableRef.orderByChild(childName).equalTo(childValue)
        q.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { s ->
                    s.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "deleteNestedEntry onCancelled")
            }
        })
    }

    fun deletePlayer(playerID: String) {
        playersRef.child(playerID).removeValue()
        deleteNestedEntry(PLAYER_ID, playerID, matchPlayerPairsRef)
        deleteNestedEntry(PLAYER_ID, playerID, playerTeamPairsRef)
        updateTimestamp(PLAYERS)
    }

    fun deleteTeam(teamID: String) {
        teamsRef.child(teamID).removeValue()
        deleteNestedEntry(TEAM_ID, teamID, matchTeamPairsRef)
        deleteNestedEntry(TEAM_ID, teamID, playerTeamPairsRef)
        updateTimestamp(TEAMS)
    }

    fun deletePlayerTeamPair(teamID: String, playerID: String) {
        val q: Query = playerTeamPairsRef.orderByChild(TEAM_ID).equalTo(teamID)
        q.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { s ->
                    val value = s.value as HashMap<*, *>
                    if(value[PLAYER_ID] == playerID) {
                        s.ref.removeValue()
                    }
                }
                updateTimestamp(PLAYER_TEAM_PAIRS)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "deletePlayerTeamPair onCancelled")
            }
        })

    }

    fun updatePlayerName(playerID: String, name: String) {
        playersRef.child(playerID).child("name").setValue(name)
        updateTimestamp(PLAYERS)
    }

    fun updateTeamName(teamID: String, name: String) {
        teamsRef.child(teamID).child("name").setValue(name)
        updateTimestamp(TEAMS)
    }

    fun matchExists(tournID: String, matchNumb: Int, callbackFun: (Boolean) -> Unit){
        val q: Query = matchesRef.orderByChild(MATCH_NUMB).equalTo(matchNumb.toString())

        q.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { s ->
                    val value = s.value as HashMap<*, *>
                    if(value[TOURNAMENT_ID] == tournID) {
                        callbackFun(true)
                    }
                }
                callbackFun(false)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "matchExists onCancelled")
            }
        })
    }




}



