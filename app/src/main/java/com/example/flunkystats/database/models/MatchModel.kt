package com.example.flunkystats.database.models

data class MatchModel (
    var matchID: String, //ID of the match
    var tournID: String?, //ID of Tournament the match belonged to
)