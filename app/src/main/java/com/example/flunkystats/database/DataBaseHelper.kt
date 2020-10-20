package com.example.flunkystats.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.flunkystats.database.models.*
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

        const val TRUE = "1"
        const val FALSE = "0"
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
                    "$COLUMN_TOURNAMENT_ID TEXT)"
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

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    private fun getFilterQuery(filterColumn: String, filterItems: List<String>?): String {
        var filterQuery: String = ""
        if (filterItems != null) {
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
        cv.put(COLUMN_NAME, player.name)

        val res = db.insert(TABLE_PLAYERS, null, cv)
        return  res != -1L
    }

    fun addTeam(team: TeamModel): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_TEAM_ID, team.teamID)
        cv.put(COLUMN_NAME, team.name)

        val res = db.insert(TABLE_TEAMS, null, cv)
        return  res != -1L
    }

    fun addMatch(match: MatchModel): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_MATCH_ID, match.matchID)
        cv.put(COLUMN_TOURNAMENT_ID, match.tournID)

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

    fun getIDandName(tableName: String): HashMap<String, String> {
        val resMap: HashMap<String, String> = HashMap()

        val db = this.writableDatabase
        val pk = getPrimaryKey(tableName)
        val query = "SELECT $pk, name FROM $tableName"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            cursor.close()
            throw Exception("Could not find Table with name $tableName")
        }
        do {
            resMap[cursor.getString(0)] = cursor.getString(1)
        } while (cursor.moveToNext())

        cursor.close()
        return resMap
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

    fun getPlayersTeams(playerID: String): HashMap<String, String> {
        val resMap = HashMap<String, String>()

        val db = this.writableDatabase
        val query = "SELECT $TABLE_TEAMS.$COLUMN_TEAM_ID, $TABLE_TEAMS.$COLUMN_NAME " +
                "FROM $TABLE_PLAYER_TEAM_PAIR, $TABLE_TEAMS " +
                "WHERE $TABLE_PLAYER_TEAM_PAIR.$COLUMN_PLAYER_ID = '$playerID' " +
                "AND $TABLE_PLAYER_TEAM_PAIR.$COLUMN_TEAM_ID = $TABLE_TEAMS.$COLUMN_TEAM_ID"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            cursor.close()
            return resMap
        }
        do {
            resMap[cursor.getString(0)] = cursor.getString(1)
        } while (cursor.moveToNext())

        cursor.close()
        return resMap
    }

    fun getPlayerHitRatio(playerID: String): Float{
        val db = this.writableDatabase
        val query = "SELECT SUM($COLUMN_SHOTS), SUM($COLUMN_HITS) " +
                "FROM $TABLE_MATCH_PLAYER_PAIR " +
                "WHERE $COLUMN_PLAYER_ID = '$playerID'"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return 0F
        }
        val shots = cursor.getInt(0).toFloat()
        val hits = cursor.getInt(1).toFloat()

        cursor.close()
        return hits/shots
    }

    fun getPlayerHitRatio(playerID: String, filterTeamIDs: List<String>?, filterTournIDs: List<String>?): Float {
        val db = this.writableDatabase
        val baseQuery =
            "SELECT SUM($TABLE_MATCH_PLAYER_PAIR.$COLUMN_SHOTS), SUM($TABLE_MATCH_PLAYER_PAIR.$COLUMN_HITS) " +
                    "FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_MATCH_TEAM_PAIR, $TABLE_MATCHES " +
                    "WHERE $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID = '$playerID' " +
                    "AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID " +
                    "AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID"

        val teamFilterQuery: String = getFilterQuery("$TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID", filterTeamIDs)

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
                "AND $COLUMN_WON = '$TRUE'"
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
                    "AND $TABLE_MATCHES.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID " +
                    "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_WON = '$TRUE' "

        val teamFilterQuery: String = getFilterQuery("$TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID", filterTeamIDs)

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

    fun getPlayerMatchStats(playerID: String): List<Int> {
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

    fun getPlayerTournStats(playerID: String): List<Int> {
        val db = this.writableDatabase
        val query = "SELECT DISTINCT $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID, $TABLE_TOURNAMENTS.$COLUMN_WINNER_TEAM_ID " +
                "FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_TOURNAMENTS, $TABLE_MATCHES " +
                "WHERE $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID = '$playerID' " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID = $TABLE_MATCHES.$COLUMN_MATCH_ID " +
                "AND $TABLE_MATCHES.$COLUMN_TOURNAMENT_ID = $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID"
        val cursor = db.rawQuery(query, null)

        val teamIDs: Set<String>? = getPlayersTeams(playerID).keys

        if (!cursor.moveToFirst()) {
            return listOf(0,0)
        }

        val tournTotal = cursor.count
        var tournWon = 0
        do {
            if(teamIDs?.contains(cursor.getString(1))!!) {
                tournWon++
            }
        } while (cursor.moveToNext())

        cursor.close()
        return listOf(tournTotal, tournWon)
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

    fun getTeamsPlayers(teamID: String): HashMap<String, String> {
        val resMap = HashMap<String, String>()

        val db = this.writableDatabase
        val query = "SELECT $TABLE_PLAYERS.$COLUMN_PLAYER_ID, $TABLE_PLAYERS.$COLUMN_NAME " +
                "FROM $TABLE_PLAYER_TEAM_PAIR, $TABLE_PLAYERS " +
                "WHERE $TABLE_PLAYER_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' " +
                "AND $TABLE_PLAYER_TEAM_PAIR.$COLUMN_PLAYER_ID = $TABLE_PLAYERS.$COLUMN_PLAYER_ID"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            cursor.close()
            return resMap
        }
        do {
            resMap[cursor.getString(0)] = cursor.getString(1)
        } while (cursor.moveToNext())

        cursor.close()
        return resMap
    }

    fun getTeamHitRatio(teamID: String): Float {
        val db = this.writableDatabase
        val query = "SELECT SUM($TABLE_MATCH_PLAYER_PAIR.$COLUMN_SHOTS), SUM($TABLE_MATCH_PLAYER_PAIR.$COLUMN_HITS) " +
                "FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_MATCH_TEAM_PAIR, $TABLE_PLAYER_TEAM_PAIR " +
                "WHERE $TABLE_PLAYER_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' " +
                "AND $TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' "+
                "AND $TABLE_PLAYER_TEAM_PAIR.$COLUMN_PLAYER_ID = $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID"
        val cursor = db.rawQuery(query, null)

        if (!cursor.moveToFirst()) {
            return 0F
        }
        val shots = cursor.getInt(0).toFloat()
        val hits = cursor.getInt(1).toFloat()

        cursor.close()
        return hits/shots
    }

    fun getTeamAvgSlugs(teamID: String): Float {
        val db = this.writableDatabase
        val query = "SELECT AVG($TABLE_MATCH_PLAYER_PAIR.$COLUMN_SLUGS) " +
                "FROM $TABLE_MATCH_PLAYER_PAIR, $TABLE_MATCH_TEAM_PAIR, $TABLE_PLAYER_TEAM_PAIR " +
                "WHERE $TABLE_PLAYER_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' " +
                "AND $TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' "+
                "AND $TABLE_PLAYER_TEAM_PAIR.$COLUMN_PLAYER_ID = $TABLE_MATCH_PLAYER_PAIR.$COLUMN_PLAYER_ID " +
                "AND $TABLE_MATCH_PLAYER_PAIR.$COLUMN_MATCH_ID = $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID " +
                "AND $TABLE_MATCH_TEAM_PAIR.$COLUMN_WON = '$TRUE'"
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

    fun getTeamTournStats(teamID: String): List<Int> {
        val db = this.writableDatabase
        val queryTotal = "SELECT COUNT (DISTINCT $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID) " +
                "FROM $TABLE_MATCH_TEAM_PAIR, $TABLE_TOURNAMENTS, $TABLE_MATCHES " +
                "WHERE $TABLE_MATCH_TEAM_PAIR.$COLUMN_TEAM_ID = '$teamID' " +
                "AND $TABLE_MATCH_TEAM_PAIR.$COLUMN_MATCH_ID = $TABLE_MATCHES.$COLUMN_MATCH_ID " +
                "AND $TABLE_MATCHES.$COLUMN_TOURNAMENT_ID = $TABLE_TOURNAMENTS.$COLUMN_TOURNAMENT_ID"
        var cursor = db.rawQuery(queryTotal, null)

        val tournTotal = if(cursor.moveToFirst()) cursor.getInt(0) else  0

        val queryWins = "SELECT COUNT($COLUMN_WINNER_TEAM_ID) " +
                "FROM $TABLE_TOURNAMENTS " +
                "WHERE $COLUMN_WINNER_TEAM_ID = '$teamID'"
        cursor = db.rawQuery(queryWins, null)

        val tournWins = if(cursor.moveToFirst()) cursor.getInt(0) else 0

        cursor.close()
        return listOf(tournTotal, tournWins)
    }
}