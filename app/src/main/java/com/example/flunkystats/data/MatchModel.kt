package com.example.flunkystats.data

data class MatchModel (
    var matchID: String, //ID of the match
    var tournID: String?, //ID of Tournament the match belonged to
    var winnerTeamID: String,
)