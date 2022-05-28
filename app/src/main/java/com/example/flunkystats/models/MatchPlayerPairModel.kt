package com.example.flunkystats.models

data class MatchPlayerPairModel (
    var matchPlayerPairID: String?,
    var matchID: String?,
    var playerID: String?,
    var shots: Int,
    var hits: Int,
    var slugs: Int,
    var won: Boolean
)