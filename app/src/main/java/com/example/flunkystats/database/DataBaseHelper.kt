package com.example.flunkystats.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.flunkystats.models.*
import java.lang.Exception

class DataBaseHelper(context: Context?) : SQLiteOpenHelper(context, "database", null, 1) {

    companion  object {
        const val TABLE_PLAYERS = "Players"
        const val TABLE_TEAMS = "Teams"
        const val TABLE_MATCHES = "Matches"
        const val TABLE_TOURNAMENTS = "Tournaments"
        const val TABLE_PLAYER_TEAM_PAIR = "PlayerTeamPairs"
        const val TABLE_MATCH_TEAM_PAIR = "MatchTeamPairs"
        const val TABLE_MATCH_PLAYER_PAIR = "MatchPlayerPairs"
        const val TABLE_TIMESTAMPS = "Timestamps"

        const val COLUMN_PLAYER_ID = "playerID"
        const val COLUMN_TEAM_ID = "teamID"
        const val COLUMN_MATCH_ID = "matchID"
        const val COLUMN_TOURNAMENT_ID = "tournID"
        const val COLUMN_MATCH_PLAYER_PAIR_ID = "matchPlayerPairID"
        const val COLUMN_MATCH_TEAM_PAIR_ID = "matchTeamPairID"
        const val COLUMN_PLAYER_TEAM_PAIR_ID = "playerTeamPairID"
        const val COLUMN_NAME = "name"
        const val COLUMN_HITS = "hits"
        const val COLUMN_SHOTS = "shots"
        const val COLUMN_SLUGS = "slugs"
        const val COLUMN_WON = "won"
        const val COLUMN_TOURNAMENT_TYPE = "tournType"
        const val COLUMN_NUMBER_OF_TEAMS = "numbTeams"
        const val COLUMN_WINNER_TEAM_ID = "winnerTeamID"
        const val COLUMN_TIMESTAMPS_ID = "timestampsID"
        const val COLUMN_MATCH_NUMB = "matchNumb"

        const val TRUE = "1"
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        var createTableStatement: String =
            "CREATE TABLE $TABLE_PLAYERS (" +
                    "$COLUMN_PLAYER_ID TEXT PRIMARY KEY, " +
                    "$COLUMN_NAME TEXT)"
        p0?.execSQL(createTableStatement)

        createTableStatement =
            "CREATE TABLE $TABLE_TEAMS (" +
                    "$COLUMN_TEAM_ID TEXT PRIMARY KEY, " +
                    "$COLUMN_NAME TEXT)"
        p0?.execSQL(createTableStatement)

        createTableStatement =
            "CREATE TABLE $TABLE_TOURNAMENTS (" +
                    "$COLUMN_TOURNAMENT_ID TEXT PRIMARY KEY, " +
                    "$COLUMN_NAME TEXT, " +
                    "$COLUMN_WINNER_TEAM_ID TEXT, " +
                    "$COLUMN_NUMBER_OF_TEAMS INTEGER, " +
                    "$COLUMN_TOURNAMENT_TYPE TEXT)"
        p0?.execSQL(createTableStatement)

        createTableStatement =
            "CREATE TABLE $TABLE_MATCHES (" +
                    "$COLUMN_MATCH_ID TEXT PRIMARY KEY, " +
                    "$COLUMN_TOURNAMENT_ID TEXT, " +
                    "$COLUMN_MATCH_NUMB INT)"
        p0?.execSQL(createTableStatement)

        createTableStatement =
            "CREATE TABLE $TABLE_PLAYER_TEAM_PAIR (" +
                    "$COLUMN_PLAYER_TEAM_PAIR_ID TEXT PRIMARY KEY, " +
                    "$COLUMN_PLAYER_ID TEXT, " +
                    "$COLUMN_TEAM_ID TEXT)"
        p0?.execSQL(createTableStatement)

        createTableStatement =
            "CREATE TABLE $TABLE_MATCH_PLAYER_PAIR (" +
                    "$COLUMN_MATCH_PLAYER_PAIR_ID TEXT PRIMARY KEY, " +
                    "$COLUMN_PLAYER_ID TEXT, " +
                    "$COLUMN_MATCH_ID TEXT, " +
                    "$COLUMN_HITS INTEGER, " +
                    "$COLUMN_SHOTS INTEGER, " +
                    "$COLUMN_SLUGS INTEGER, " +
                    "$COLUMN_WON INTEGER)"
        p0?.execSQL(createTableStatement)

        createTableStatement =
            "CREATE TABLE $TABLE_MATCH_TEAM_PAIR (" +
                    "$COLUMN_MATCH_TEAM_PAIR_ID TEXT PRIMARY KEY, " +
                    "$COLUMN_TEAM_ID TEXT, " +
                    "$COLUMN_MATCH_ID TEXT, " +
                    "$COLUMN_WON INTEGER)"
        p0?.execSQL(createTableStatement)

        createTableStatement =
            "CREATE TABLE $TABLE_TIMESTAMPS (" +
                    "$COLUMN_TIMESTAMPS_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$TABLE_PLAYERS INTEGER, " +
                    "$TABLE_TEAMS INTEGER, " +
                    "$TABLE_MATCHES INTEGER, " +
                    "$TABLE_TOURNAMENTS INTEGER, " +
                    "$TABLE_PLAYER_TEAM_PAIR INTEGER, " +
                    "$TABLE_MATCH_PLAYER_PAIR INTEGER, " +
                    "$TABLE_MATCH_TEAM_PAIR INTEGER)"
        p0?.execSQL(createTableStatement)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    private fun getFilterQuery(filterColumn: String, filterItems: List<String>?): String {
        var filterQuery = ""
        if (filterItems != null && filterItems.isNotEmpty()) {
            filterQuery += " AND ("
            for (item in filterItems) {
                filterQuery += "$filterColumn = '$item'"
                if (item != filterItems.last()) {
                    filterQuery += " OR "
                }
            }
            filterQuery += ") "
        }
        return filterQuery
    }

    fun addPlayer(player: PlayerModel): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_PLAYER_ID, player.playerID)
        cv.put(COLUMN_NAME, player.playerName)

        val res = db.insert(TABLE_PLAYERS, null, cv)
        return  res != -1L
    }

    fun addTeam(team: TeamModel): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_TEAM_ID, team.teamID)
        cv.put(COLUMN_NAME, team.teamName)

        val res = db.insert(TABLE_TEAMS, null, cv)
        return  res != -1L
    }

    fun addMatch(match: MatchModel): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_MATCH_ID, match.matchID)
        cv.put(COLUMN_TOURNAMENT_ID, match.tournID)
        cv.put(COLUMN_MATCH_NUMB, match.matchNumb)

        val res = db.insert(TABLE_MATCHES, null, cv)
        return  res != -1L
    }

    fun addTournament(tourn: TournamentModel): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_TOURNAMENT_ID, tourn.tournID)
        cv.put(COLUMN_WINNER_TEAM_ID, tourn.winnerTeamID)
        cv.put(COLUMN_NAME, tourn.name)
        cv.put(COLUMN_NUMBER_OF_TEAMS, tourn.numbTeams)
        cv.put(COLUMN_TOURNAMENT_TYPE, tourn.tournType)

        val res = db.insert(TABLE_TOURNAMENTS, null, cv)
        return res != -1L
    }

    fun addPlayerTeamPair(playerTeamPair: PlayerTeamPairModel): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_PLAYER_TEAM_PAIR_ID, playerTeamPair.playerTeamPairID)
        cv.put(COLUMN_PLAYER_ID, playerTeamPair.playerID)
        cv.put(COLUMN_TEAM_ID, playerTeamPair.teamID)

        val res = db.insert(TABLE_PLAYER_TEAM_PAIR, null, cv)
        return res != -1L
    }

    fun addMatchPlayerPair(matchPlayerPair: MatchPlayerPairModel): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_MATCH_PLAYER_PAIR_ID, matchPlayerPair.matchPlayerPairID)
        cv.put(COLUMN_PLAYER_ID, matchPlayerPair.playerID)
        cv.put(COLUMN_MATCH_ID, matchPlayerPair.matchID)
        cv.put(COLUMN_HITS, matchPlayerPair.hits)
        cv.put(COLUMN_SHOTS, matchPlayerPair.shots)
        cv.put(COLUMN_SLUGS, matchPlayerPair.slugs)
        cv.put(COLUMN_WON, matchPlayerPair.won)

        val res = db.insert(TABLE_MATCH_PLAYER_PAIR, null, cv)
        return res != -1L
    }

    fun addMatchTeamPair(matchTeamPair: MatchTeamPairModel): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_MATCH_TEAM_PAIR_ID, matchTeamPair.matchTeamPairID)
        cv.put(COLUMN_TEAM_ID, matchTeamPair.teamID)
        cv.put(COLUMN_MATCH_ID, matchTeamPair.matchID)
        cv.put(COLUMN_WON, matchTeamPair.won)

        val res = db.insert(TABLE_MATCH_TEAM_PAIR, null, cv)
        return res != -1L
    }

    fun addTimestamps(timestamp: TimestampsModel): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(TABLE_PLAYERS, timestamp.playersTS)
        cv.put(TABLE_TEAMS, timestamp.teamsTS)
        cv.put(TABLE_MATCHES, timestamp.matchesTS)
        cv.put(TABLE_TOURNAMENTS, timestamp.tournTS)
        cv.put(TABLE_PLAYER_TEAM_PAIR, timestamp.playerTeamTS)
        cv.put(TABLE_MATCH_PLAYER_PAIR, timestamp.matchPlayerTS)
        cv.put(TABLE_MATCH_TEAM_PAIR, timestamp.matchTeamTS)

        val res = db.insert(TABLE_TIMESTAMPS, null, cv)
        return res != -1L
    }

    fun clearTable(tableName: String) {
        val db = this.writableDatabase

        val delTableStmt = "DELETE FROM $tableName"

        db.execSQL(delTableStmt)
    }

    fun getTimestamps(): ArrayList<Int>? {
        val db = this.writableDatabase
        val query = "SELECT * FROM $TABLE_TIMESTAMPS"
        val cursor = db.rawQuery(query, null)
        val resList :ArrayList<Int> = ArrayList()

        if(!cursor.moveToFirst()) {
            cursor.close()
            return null
        }
        for (i in 1 until cursor.columnCount) {
            resList.add(cursor.getInt(i))
        }
        cursor.close()
        return resList
    }

    private fun getPrimaryKey(tableName: String): String? {
        val db = this.writableDatabase
        val query = "PRAGMA table_info($tableName)"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            cursor.close()
            throw Exception("Could not find Table with name $tableName")
        }
        do {
            if (cursor.getString(5) == "1") {
                val res = cursor.getString(1)
                cursor.close()
                return res
            }
        } while (cursor.moveToNext())

        cursor.close()
        throw Exception("Could not find primary key of table $tableName")
    }

    fun getIDandName(tableName: String): List<EntryModel> {
        val resList = arrayListOf<EntryModel>()

        val db = this.writableDatabase
        val pk = getPrimaryKey(tableName)
        val query = "SELECT $pk, name FROM $tableName"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            cursor.close()
            throw Exception("Could not find Table with name $tableName")
        }
        do {
            resList.add(EntryModel(cursor.getString(0), cursor.getString(1)))
        } while (cursor.moveToNext())

        cursor.close()
        return resList
    }

    fun getPlayerName(playerID: String): String {
        val db = this.writableDatabase
        val query = "SELECT $COLUMN_NAME FROM $TABLE_PLAYERS WHERE $COLUMN_PLAYER_ID = '$playerID'"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            cursor.close()
            throw Exception("Could not find Player with ID $playerID")
        }
        val res = cursor.getString(0)
        cursor.close()
        return res
    }

    fun getPlayersTeams(playerID: String): List<TeamModel> {
        val resList = arrayListOf<TeamModel>()

        val db = this.writableDatabase
        val query = "SELECT $TABLE_TEAMS.$COLUMN_TEAM_ID, $TABLE_TEAMS.$COLUMN_NAME " +
                "FROM $TABLE_PLAYER_TEAM_PAIR, $TABLE_TEAMS " +
                "WHERE $TABLE_PLAYER_TEAM_PAIR.$COLUMN_PLAYER_ID = '$playerID' " +
                "AND $TABLE_PLAYER_TEAM_PAIR.$COLUMN_TEAM_ID = $TABLE_TEAMS.$COLUMN_TEAM_ID"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            cursor.close()
            return resList
        }
        do {
            resList.add(TeamModel(cursor.getString(0), cursor.getString(1)))
        } while (cursor.moveToNext())

        cursor.close()
        return resList
    }

    fun getPlayersTourns(playerID: String): List<TournamentModel> {
        val resList = arrayListOf<TournamentModel>()

        val db = this.writableDatabase
        val query = "SELECT DISTINCT $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID, $TABLE_TOURNAMENTS.$COLUMN_NAME " +
                "FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_TOURNAMENTS, $TABLE_MATCHES " +
                "WHERE $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID = '$playerID' " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID = $TABLE_MATCHES.$COLUMN_MATCH_ID " +
                "AND $TABLE_MATCHES.$COLUMN_TOURNAMENT_ID = $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            cursor.close()
            return resList
        }
        do {
            resList.add(TournamentModel(tournID = cursor.getString(0), name = cursor.getString(1)))
        } while (cursor.moveToNext())

        cursor.close()
        return resList
    }

    fun getPlayerSumShotsStats(playerID: String): List<Int> {
        val db = this.writableDatabase
        val query = "SELECT SUM($COLUMN_SHOTS), SUM($COLUMN_HITS) " +
                "FROM $TABLE_MATCH_PLAYER_PAIR " +
                "WHERE $COLUMN_PLAYER_ID = '$playerID' " +
                "AND $COLUMN_HITS != -1 " +
                "AND $COLUMN_SHOTS != -1 "
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return listOf(0,0)
        }
        val shots = cursor.getInt(0)
        val hits = cursor.getInt(1)

        cursor.close()
        return listOf(shots, hits)
    }

    fun getPlayerSumShotsStats(playerID: String , filterTournIDs: List<String>?): List<Int> {
        val db = this.writableDatabase
        val baseQuery =
            "SELECT SUM($TABLE_MATCH_PLAYER_PAIR.$COLUMN_SHOTS), SUM($TABLE_MATCH_PLAYER_PAIR.$COLUMN_HITS) " +
                    "FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_MATCHES " +
                    "WHERE $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID = '$playerID' " +
                    "AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID " +
                    "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_HITS != -1 " +
                    "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_SHOTS != -1 "

        val tournFilterQuery: String = getFilterQuery("$TABLE_MATCHES.$COLUMN_TOURNAMENT_ID", filterTournIDs)

        val query = baseQuery + tournFilterQuery

        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return listOf(0,0)
        }
        val shots = cursor.getInt(0)
        val hits = cursor.getInt(1)

        cursor.close()
        return listOf(shots, hits)
    }

    fun getPlayerHitRatio(playerID: String): Float{
        val db = this.writableDatabase
        val query = """SELECT SUM($COLUMN_SHOTS), SUM($COLUMN_HITS) 
                FROM $TABLE_MATCH_PLAYER_PAIR 
                WHERE $COLUMN_PLAYER_ID = '$playerID' 
                AND $COLUMN_HITS != -1 
                AND $COLUMN_SHOTS != -1 """
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return 0F
        }
        val shots = cursor.getInt(0).toFloat()
        val hits = cursor.getInt(1).toFloat()
        cursor.close()

        return if(hits == -1F || shots == -1F)
            -1F
        else
            hits/shots
    }

    fun getPlayerHitRatio(playerID: String, filterTeamIDs: List<String>?, filterTournIDs: List<String>?): Float {
        val db = this.writableDatabase
        val baseQuery =
            """SELECT SUM($TABLE_MATCH_PLAYER_PAIR.$COLUMN_SHOTS), SUM($TABLE_MATCH_PLAYER_PAIR.$COLUMN_HITS) 
                    FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_MATCH_TEAM_PAIR, $TABLE_MATCHES 
                    WHERE $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID = '$playerID' 
                    AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID 
                    AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_HITS != -1 
                    AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_SHOTS != -1 """


        val teamFilterQuery: String = getFilterQuery("$TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID", filterTeamIDs) +
                "AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID "

        val tournFilterQuery: String = getFilterQuery("$TABLE_MATCHES.$COLUMN_TOURNAMENT_ID", filterTournIDs)

        val query = baseQuery + teamFilterQuery + tournFilterQuery

        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return 0F
        }
        val shots = cursor.getInt(0).toFloat()
        val hits = cursor.getInt(1).toFloat()

        cursor.close()
        return hits/shots
    }

    fun getPlayerAvgSlugs(playerID: String): Float {
        val db = this.writableDatabase
        val query = "SELECT AVG($COLUMN_SLUGS)" +
                "FROM $TABLE_MATCH_PLAYER_PAIR " +
                "WHERE $COLUMN_PLAYER_ID = '$playerID' " +
                "AND $COLUMN_WON = '$TRUE' " +
                "AND $COLUMN_SLUGS != -1 "
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return 0F
        }

        val res = cursor.getFloat(0)
        cursor.close()
        return res
    }

    fun getPlayerAvgSlugs(playerID: String, filterTeamIDs: List<String>?, filterTournIDs: List<String>?): Float {
        val db = this.writableDatabase
        val baseQuery =
            "SELECT AVG($TABLE_MATCH_PLAYER_PAIR.$COLUMN_SLUGS) " +
                    "FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_MATCH_TEAM_PAIR, $TABLE_MATCHES " +
                    "WHERE $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID = '$playerID' " +
                    "AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID " +
                    "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_WON = '$TRUE' " +
                    "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_SLUGS != -1 "

        val teamFilterQuery: String = if (filterTeamIDs != null) {
            getFilterQuery("$TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID", filterTeamIDs) +
                    "AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID "
        } else ""

        val tournFilterQuery: String = getFilterQuery("$TABLE_MATCHES.$COLUMN_TOURNAMENT_ID", filterTournIDs)

        val query = baseQuery + teamFilterQuery + tournFilterQuery

        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return 0F
        }

        val res = cursor.getFloat(0)
        cursor.close()
        return res
    }

    fun getPlayerMatchNumbers(playerID: String): List<Int> {
        val db = this.writableDatabase
        val query = "SELECT $COLUMN_WON " +
                "FROM $TABLE_MATCH_PLAYER_PAIR " +
                "WHERE $COLUMN_PLAYER_ID = '$playerID'"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return listOf(0,0)
        }
        val gamesTotal = cursor.count
        var gamesWon = 0
        do {
            if(cursor.getInt(0) == 1) {
                gamesWon++
            }
        } while (cursor.moveToNext())

        cursor.close()
        return listOf(gamesTotal, gamesWon)
    }

    fun getPlayerMatchNumbers(playerID: String, filterTeamIDs: List<String>?, filterTournIDs: List<String>?): List<Int> {
        val db = this.writableDatabase
        val baseQuery =
            "SELECT DISTINCT $TABLE_MATCH_PLAYER_PAIR.$COLUMN_WON, $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID " +
                    "FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_MATCH_TEAM_PAIR, $TABLE_MATCHES " +
                    "WHERE $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID = '$playerID' " +
                    "AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID "

        val teamFilterQuery: String = if (filterTeamIDs != null) {
            getFilterQuery("$TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID", filterTeamIDs) +
                    "AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID "
        } else ""

        val tournFilterQuery: String = getFilterQuery("$TABLE_MATCHES.$COLUMN_TOURNAMENT_ID", filterTournIDs)

        val query = baseQuery + teamFilterQuery + tournFilterQuery
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return listOf(0,0)
        }
        val gamesTotal = cursor.count
        var gamesWon = 0
        do {
            if(cursor.getInt(0) == 1) {
                gamesWon++
            }
        } while (cursor.moveToNext())

        cursor.close()
        return listOf(gamesTotal, gamesWon)
    }

    fun getPlayerTournStats(playerID: String, filterTeamIDs: List<String>?, filterTournIDs: List<String>?): List<Int> {
        val db = this.writableDatabase
        val baseQuery = "SELECT DISTINCT $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID, $TABLE_TOURNAMENTS.$COLUMN_WINNER_TEAM_ID " +
                "FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_TOURNAMENTS, $TABLE_MATCHES, $TABLE_MATCH_TEAM_PAIR " +
                "WHERE $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID = '$playerID' " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID = $TABLE_MATCHES.$COLUMN_MATCH_ID " +
                "AND $TABLE_MATCHES.$COLUMN_TOURNAMENT_ID = $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID "

        val teamFilterQuery: String = if (filterTeamIDs != null) {
            getFilterQuery("$TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID", filterTeamIDs) +
                    "AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID "
        } else ""

        val tournFilterQuery: String = getFilterQuery("$TABLE_MATCHES.$COLUMN_TOURNAMENT_ID", filterTournIDs)

        val query = baseQuery + teamFilterQuery + tournFilterQuery

        val cursor = db.rawQuery(query, null)

        val playersTeamIDs = arrayListOf<String>()
        getPlayersTeams(playerID).forEach {
            playersTeamIDs.add(it.teamID)
        }
        val teamIDs: List<String> = filterTeamIDs ?: playersTeamIDs

        if (!cursor.moveToFirst()) {
            return listOf(0,0)
        }

        val tournTotal = cursor.count
        var tournWon = 0
        do {
            if(teamIDs.contains(cursor.getString(1))) {
                tournWon++
            }
        } while (cursor.moveToNext())

        cursor.close()
        return listOf(tournTotal, tournWon)
    }

    fun getPlayerTournStats(playerID: String): List<Int> {
        val db = this.writableDatabase
        val query = "SELECT DISTINCT $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID, $TABLE_TOURNAMENTS.$COLUMN_WINNER_TEAM_ID " +
                "FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_TOURNAMENTS, $TABLE_MATCHES " +
                "WHERE $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID = '$playerID' " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID = $TABLE_MATCHES.$COLUMN_MATCH_ID " +
                "AND $TABLE_MATCHES.$COLUMN_TOURNAMENT_ID = $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return listOf(0,0)
        }

        val teamIDs = arrayListOf<String>()
        getPlayersTeams(playerID).forEach {
            teamIDs.add(it.teamID)
        }

        val tournTotal = cursor.count
        var tournWon = 0
        do {
            if(teamIDs.contains(cursor.getString(1))) {
                tournWon++
            }
        } while (cursor.moveToNext())

        cursor.close()
        return listOf(tournTotal, tournWon)
    }

    fun getPlayerListData(): ArrayList<ListEntryModel>? {
        val resList: ArrayList<ListEntryModel> = arrayListOf()
        val players = getIDandName(TABLE_PLAYERS)
        players.forEach { (id, name) ->
            val teams = arrayListOf<String>()
            getPlayersTeams(id).forEach { it.teamName?.let { it1 -> teams.add(it1) } }
            val entry = ListEntryModel(name, id, teams)
            resList.add(entry)
        }
        resList.sortBy { it.entryName }
        return resList
    }

    fun getTeamListData(): ArrayList<ListEntryModel>? {
        val resList: ArrayList<ListEntryModel> = arrayListOf()
        val teams = getIDandName(TABLE_TEAMS)
        teams.forEach { team ->
            val players  = arrayListOf<String>()
            getTeamsPlayers(team.entryID).forEach { players.add(it.playerName ?: "ERROR") }
            val entry = ListEntryModel(team.entryName, team.entryID, players)
            resList.add(entry)
        }
        resList.sortBy { it.entryName }
        return resList
    }

    fun getMatchListData(): ArrayList<ListMatchModel>? {
        val db = this.writableDatabase
        val query = """SELECT $COLUMN_MATCH_ID, $COLUMN_MATCH_NUMB FROM $TABLE_MATCHES"""
        val cursor = db.rawQuery(query, null)

        if(!cursor.moveToFirst()) {
            cursor.close()
            return arrayListOf()
        }

        val resList: ArrayList<ListMatchModel> = arrayListOf()
        do {
            val matchID = cursor.getString(0)
            val matchNumb = cursor.getInt(1)
            val teams = getMatchesTeams(matchID)

            val tournName = getMatchesTournName(matchID)
            val entry = ListMatchModel(
                matchID = matchID,
                matchNumb = matchNumb,
                team1ID = teams[0].teamID,
                team1Name = teams[0].teamName ?: "ERROR",
                team2ID = teams[1].teamID,
                team2Name = teams[1].teamName ?: "ERROR",
                winnerID = getWinnerTeamID(matchID),
                matchInfo = listOf(tournName)
            )
            resList.add(entry)
        } while (cursor.moveToNext())

        cursor.close()
        return resList
    }

    fun getMatchListData(tournID: String): ArrayList<ListMatchModel> {
        val db = this.writableDatabase
        val query = """SELECT $COLUMN_MATCH_ID, $COLUMN_MATCH_NUMB FROM $TABLE_MATCHES WHERE $COLUMN_TOURNAMENT_ID = '$tournID'"""
        val cursor = db.rawQuery(query, null)

        if(!cursor.moveToFirst()) {
            cursor.close()
            return arrayListOf()
        }

        val tournName = getTournName(tournID)

        val resList: ArrayList<ListMatchModel> = arrayListOf()
        do {
            val matchID = cursor.getString(0)
            val matchNumb = cursor.getInt(1)
            val teams = getMatchesTeams(matchID)

            val entry = ListMatchModel(
                matchID = matchID,
                matchNumb = matchNumb,
                team1ID = teams[0].teamID,
                team1Name = teams[0].teamName ?: "ERROR",
                team2ID = teams[1].teamID,
                team2Name = teams[1].teamName ?: "ERROR",
                winnerID = getWinnerTeamID(matchID),
                matchInfo = listOf(tournName)
            )
            resList.add(entry)
        } while (cursor.moveToNext())

        cursor.close()
        return resList
    }

    fun getMatchesTeams(matchID: String): List<TeamModel> {
        val db = this.writableDatabase
        val query = """SELECT DISTINCT $TABLE_TEAMS.$COLUMN_TEAM_ID, $TABLE_TEAMS.$COLUMN_NAME 
                FROM $TABLE_TEAMS, $TABLE_MATCH_TEAM_PAIR 
                WHERE $TABLE_TEAMS.$COLUMN_TEAM_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID 
                AND $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID = '$matchID'"""
        val cursor = db.rawQuery(query, null)

        if(!cursor.moveToFirst() || cursor.count != 2) {
            return listOf()
        }

        val id1 = cursor.getString(0)
        val name1 = cursor.getString(1)
        cursor.moveToNext()
        val id2 = cursor.getString(0)
        val name2 = cursor.getString(1)
        cursor.close()
        return listOf(TeamModel(id1,name1), TeamModel(id2, name2))
    }

    private fun getMatchesTournName(matchID: String): String {
        val db = this.writableDatabase
        val query = """SELECT DISTINCT $TABLE_TOURNAMENTS.$COLUMN_NAME 
                FROM $TABLE_TOURNAMENTS, $TABLE_MATCHES 
                WHERE $TABLE_MATCHES.$COLUMN_TOURNAMENT_ID = $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID 
                AND $TABLE_MATCHES.$COLUMN_MATCH_ID = '$matchID'"""
        val cursor = db.rawQuery(query, null)

        if(!cursor.moveToFirst() || cursor.count != 1) {
            return "ERROR"
        }
        val res = cursor.getString(0)
        cursor.close()
        return res
    }

    fun getTeamName(teamID: String): String {
        val db = this.writableDatabase
        val query = "SELECT $COLUMN_NAME FROM $TABLE_TEAMS WHERE $COLUMN_TEAM_ID = '$teamID'"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            cursor.close()
            throw Exception("Could not find Team with ID $teamID")
        }
        val res = cursor.getString(0)
        cursor.close()
        return res
    }

    fun getTeamsPlayers(teamID: String): List<PlayerModel> {
        val resList = arrayListOf<PlayerModel>()

        val db = this.writableDatabase
        val query = "SELECT $TABLE_PLAYERS.$COLUMN_PLAYER_ID, $TABLE_PLAYERS.$COLUMN_NAME " +
                "FROM $TABLE_PLAYER_TEAM_PAIR, $TABLE_PLAYERS " +
                "WHERE $TABLE_PLAYER_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' " +
                "AND $TABLE_PLAYER_TEAM_PAIR.$COLUMN_PLAYER_ID = $TABLE_PLAYERS.$COLUMN_PLAYER_ID"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst() || cursor.count != 2) {
            cursor.close()
            return resList
        }
        do {
            resList.add(PlayerModel(cursor.getString(0), cursor.getString(1)))
        } while (cursor.moveToNext())

        cursor.close()
        return resList
    }

    fun getTeamSumShotsStats(teamID: String, filterTournIDs: List<String>? = null): List<Int> {
        val db = this.writableDatabase
        val baseQuery = "SELECT SUM($TABLE_MATCH_PLAYER_PAIR.$COLUMN_SHOTS), SUM($TABLE_MATCH_PLAYER_PAIR.$COLUMN_HITS) " +
                "FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_MATCH_TEAM_PAIR, $TABLE_PLAYER_TEAM_PAIR, $TABLE_MATCHES " +
                "WHERE $TABLE_PLAYER_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' " +
                "AND $TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' "+
                "AND $TABLE_PLAYER_TEAM_PAIR.$COLUMN_PLAYER_ID = $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_HITS != -1 " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_SHOTS != -1 "

        var tournFilterQuery = ""
        if(filterTournIDs != null) {
            tournFilterQuery = " AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID "
            tournFilterQuery += getFilterQuery("$TABLE_MATCHES.$COLUMN_TOURNAMENT_ID", filterTournIDs)
        }

        val query = baseQuery + tournFilterQuery

        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return listOf(0,0)
        }
        val shots = cursor.getInt(0)
        val hits = cursor.getInt(1)

        cursor.close()
        return listOf(shots, hits)
    }

    fun getTeamHitRatio(teamID: String, filterTournIDs: List<String>? = null): Float {
        val db = this.writableDatabase
        val baseQuery = "SELECT SUM($TABLE_MATCH_PLAYER_PAIR.$COLUMN_SHOTS), SUM($TABLE_MATCH_PLAYER_PAIR.$COLUMN_HITS) " +
                "FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_MATCH_TEAM_PAIR, $TABLE_PLAYER_TEAM_PAIR, $TABLE_MATCHES " +
                "WHERE $TABLE_PLAYER_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' " +
                "AND $TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' "+
                "AND $TABLE_PLAYER_TEAM_PAIR.$COLUMN_PLAYER_ID = $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_HITS != -1 " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_SHOTS != -1 "

        var tournFilterQuery = ""
        if(filterTournIDs != null) {
            tournFilterQuery = " AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID "
            tournFilterQuery += getFilterQuery("$TABLE_MATCHES.$COLUMN_TOURNAMENT_ID", filterTournIDs)
        }

        val query = baseQuery + tournFilterQuery

        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return 0F
        }
        val shots = cursor.getInt(0).toFloat()
        val hits = cursor.getInt(1).toFloat()

        cursor.close()
        return hits/shots
    }

    fun getTeamAvgSlugs(teamID: String, filterTournIDs: List<String>? = null): Float {
        val db = this.writableDatabase
        val baseQuery = "SELECT AVG($TABLE_MATCH_PLAYER_PAIR.$COLUMN_SLUGS) " +
                "FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_MATCH_TEAM_PAIR, $TABLE_PLAYER_TEAM_PAIR, $TABLE_MATCHES " +
                "WHERE $TABLE_PLAYER_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' " +
                "AND $TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' "+
                "AND $TABLE_PLAYER_TEAM_PAIR.$COLUMN_PLAYER_ID = $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID " +
                "AND $TABLE_MATCH_TEAM_PAIR.$COLUMN_WON = '$TRUE' " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_SLUGS != -1 "

        var tournFilterQuery = ""
        if(filterTournIDs != null) {
            tournFilterQuery = " AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID "
            tournFilterQuery += getFilterQuery("$TABLE_MATCHES.$COLUMN_TOURNAMENT_ID", filterTournIDs)
        }

        val query = baseQuery + tournFilterQuery

        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return 0F
        }

        val res = cursor.getFloat(0)
        cursor.close()
        return res
    }

    fun getTeamMatchStats(teamID: String): List<Int> {
        val db = this.writableDatabase
        val query = "SELECT $COLUMN_WON " +
                "FROM $TABLE_MATCH_TEAM_PAIR " +
                "WHERE $COLUMN_TEAM_ID = '$teamID'"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return listOf(0,0)
        }
        val gamesTotal = cursor.count
        var gamesWon = 0
        do {
            if(cursor.getInt(0) == 1) {
                gamesWon++
            }
        } while (cursor.moveToNext())

        cursor.close()
        return listOf(gamesTotal, gamesWon)
    }

    fun getTeamMatchStats(teamID: String, filterTournIDs: List<String>): List<Int> {
        val db = this.writableDatabase
        val baseQuery =
            "SELECT DISTINCT $TABLE_MATCH_TEAM_PAIR.$COLUMN_WON, $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID " +
                    "FROM $TABLE_MATCH_TEAM_PAIR, $TABLE_MATCHES " +
                    "WHERE $TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' " +
                    "AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID "


        val tournFilterQuery: String = getFilterQuery("$TABLE_MATCHES.$COLUMN_TOURNAMENT_ID", filterTournIDs)

        val query = baseQuery + tournFilterQuery

        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return listOf(0,0)
        }
        val gamesTotal = cursor.count
        var gamesWon = 0
        do {
            if(cursor.getInt(0) == 1) {
                gamesWon++
            }
        } while (cursor.moveToNext())

        cursor.close()
        return listOf(gamesTotal, gamesWon)
    }

    fun getTeamTournStats(teamID: String, filterTournIDs: List<String>? = null): List<Int> {
        val db = this.writableDatabase
        val baseQueryTotal = "SELECT COUNT (DISTINCT $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID) " +
                "FROM $TABLE_MATCH_TEAM_PAIR, $TABLE_TOURNAMENTS, $TABLE_MATCHES " +
                "WHERE $TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' " +
                "AND $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID = $TABLE_MATCHES.$COLUMN_MATCH_ID " +
                "AND $TABLE_MATCHES.$COLUMN_TOURNAMENT_ID = $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID"

        val tournFilterQueryTotal: String = getFilterQuery("$TABLE_MATCHES.$COLUMN_TOURNAMENT_ID", filterTournIDs)

        val queryTotal = baseQueryTotal + tournFilterQueryTotal

        var cursor = db.rawQuery(queryTotal, null)

        val tournTotal = if(cursor.moveToFirst()) cursor.getInt(0) else  0

        val baseQueryWins = "SELECT COUNT($COLUMN_WINNER_TEAM_ID) " +
                "FROM $TABLE_TOURNAMENTS " +
                "WHERE $COLUMN_WINNER_TEAM_ID = '$teamID'"

        val tournFilterQueryWins: String = getFilterQuery("$TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID", filterTournIDs)

        val queryWins = baseQueryWins + tournFilterQueryWins

        cursor = db.rawQuery(queryWins, null)

        val tournWins = if(cursor.moveToFirst()) cursor.getInt(0) else 0

        cursor.close()
        return listOf(tournTotal, tournWins)
    }

    fun getTeamsTourns(teamID: String): List<TournamentModel> {
        val resList = arrayListOf<TournamentModel>()

        val db = this.writableDatabase
        val query = "SELECT DISTINCT $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID, $TABLE_TOURNAMENTS.$COLUMN_NAME " +
                "FROM $TABLE_MATCH_TEAM_PAIR, $TABLE_TOURNAMENTS, $TABLE_MATCHES " +
                "WHERE $TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' " +
                "AND $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID = $TABLE_MATCHES.$COLUMN_MATCH_ID " +
                "AND $TABLE_MATCHES.$COLUMN_TOURNAMENT_ID = $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            cursor.close()
            return resList
        }
        do {
            resList.add(TournamentModel(tournID = cursor.getString(0), name = cursor.getString(1)))
        } while (cursor.moveToNext())

        cursor.close()
        return resList
    }

    fun getPlayerMatchStats(playerID: String, matchID: String): PlayerMatchStatsModel {
        val db = this.writableDatabase
        val query = """SELECT $COLUMN_HITS, $COLUMN_SHOTS, $COLUMN_SLUGS, $COLUMN_WON 
            FROM $TABLE_MATCH_PLAYER_PAIR 
            WHERE $COLUMN_MATCH_ID = '$matchID' 
            AND $COLUMN_PLAYER_ID = '$playerID'"""
        val cursor = db.rawQuery(query, null)

        if(!cursor.moveToNext() || cursor.count != 1) {
            throw NullPointerException("Could not find stats for player with ID $playerID and match with ID $matchID")
        }

        val res = PlayerMatchStatsModel(
            playerID = playerID,
            playerName = getPlayerName(playerID),
            hits = cursor.getInt(0),
            shots = cursor.getInt(1),
            slugs = cursor.getInt(2),
            won = cursor.getInt(3) == 1
        )
        cursor.close()
        return res
    }

    fun getTournName(tournID: String): String {
        val db = this.writableDatabase
        val query = "SELECT $COLUMN_NAME FROM $TABLE_TOURNAMENTS WHERE $COLUMN_TOURNAMENT_ID = '$tournID'"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            cursor.close()
            throw Exception("Could not find Team with ID $tournID")
        }
        val res = cursor.getString(0)
        cursor.close()
        return res
    }

    fun getWinnerTeamID(matchID: String): String {
        val db = this.writableDatabase
        val query = """SELECT $COLUMN_TEAM_ID FROM $TABLE_MATCH_TEAM_PAIR WHERE $COLUMN_MATCH_ID = '$matchID' AND $COLUMN_WON = '1'"""
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            cursor.close()
            throw Exception("Could not find Winner of Match with ID $matchID")
        }
        val res = cursor.getString(0)
        cursor.close()
        return res
    }
}