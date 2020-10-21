package com.example.flunkystats.models

data class MatchTeamPairModel (
    var matchTeamPairID: String,
    var matchID: String?,
    var teamID: String?,
    var won: Boolean?
)