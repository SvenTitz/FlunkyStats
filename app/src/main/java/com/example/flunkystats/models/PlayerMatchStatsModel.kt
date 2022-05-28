package com.example.flunkystats.models

data class PlayerMatchStatsModel (
    val playerID: String?,
    val playerName: String,
    val shots: Int,
    val hits: Int,
    val slugs: Int,
    val won: Boolean,
    var teamName: String? = null,
    var teamID: String? = null,
    var matchPlayerID: String? = null,
    var matchTeamID: String? = null
)
