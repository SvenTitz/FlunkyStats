package com.example.flunkystats.data

data class MatchTeamPairModel (
    var matchTeamPairID: String,
    var matchID: String,
    var teamID: String,
    var shots: Int,
    var hits: Int,
    var slugs: Int,
    var won: Boolean
)