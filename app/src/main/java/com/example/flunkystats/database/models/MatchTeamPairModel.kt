package com.example.flunkystats.database.models

data class MatchTeamPairModel (
    var matchTeamPairID: String,
    var matchID: String?,
    var teamID: String?,
    var won: Boolean?
)