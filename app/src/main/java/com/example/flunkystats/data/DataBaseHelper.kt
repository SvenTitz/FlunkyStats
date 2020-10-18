package com.example.flunkystats.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context?) : SQLiteOpenHelper(context, "database", null, 1) {

    companion  object {
        const val TABLE_PLAYERS = "Players"
        const val TABLE_TEAMS = "Teams"
        const val TABLE_MATCHES = "Matches"
        const val TABLE_TOURNAMENTS = "Tournaments"
        const val TABLE_PLAYER_TEAM_PAIR = "PlayerTeamPairs"
        const val TABLE_MATCH_TEAM_PAIR = "MatchTeamPairs"
        const val TABLE_MATCH_PLAYER_PAIR = "MatchPlayerPairs"

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
            "CREATE TABLE $TABLE_MATCHES (" +
                    "$COLUMN_MATCH_ID TEXT PRIMARY KEY, " +
                    "$COLUMN_TOURNAMENT_ID TEXT, " +
                    "$COLUMN_WINNER_TEAM_ID TEXT)"
        p0?.execSQL(createTableStatement)

        createTableStatement =
            "CREATE TABLE $TABLE_TOURNAMENTS (" +
                    "$COLUMN_TOURNAMENT_ID TEXT PRIMARY KEY, " +
                    "$COLUMN_NAME TEXT, " +
                    "$COLUMN_WINNER_TEAM_ID TEXT, " +
                    "$COLUMN_NUMBER_OF_TEAMS INT, " +
                    "$COLUMN_TOURNAMENT_TYPE TEXT)"
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
                    "$COLUMN_HITS INT, " +
                    "$COLUMN_SHOTS INT, " +
                    "$COLUMN_SLUGS INT, " +
                    "$COLUMN_WON INT)"
        p0?.execSQL(createTableStatement)

        createTableStatement =
            "CREATE TABLE $TABLE_MATCH_TEAM_PAIR (" +
                    "$COLUMN_MATCH_TEAM_PAIR_ID TEXT PRIMARY KEY, " +
                    "$COLUMN_TEAM_ID TEXT, " +
                    "$COLUMN_MATCH_ID TEXT, " +
                    "$COLUMN_HITS INT, " +
                    "$COLUMN_SHOTS INT, " +
                    "$COLUMN_SLUGS INT, " +
                    "$COLUMN_WON INT)"
        p0?.execSQL(createTableStatement)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
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
        cv.put(COLUMN_WINNER_TEAM_ID, match.winnerTeamID)

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
        cv.put(COLUMN_HITS, matchTeamPair.hits)
        cv.put(COLUMN_SHOTS, matchTeamPair.shots)
        cv.put(COLUMN_SLUGS, matchTeamPair.slugs)
        cv.put(COLUMN_WON, matchTeamPair.won)

        val res = db.insert(TABLE_MATCH_TEAM_PAIR, null, cv)
        return res != -1L
    }

}