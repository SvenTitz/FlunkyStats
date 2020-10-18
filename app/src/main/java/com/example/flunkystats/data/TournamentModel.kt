package com.example.flunkystats.data

@Suppress("unused")
data class TournamentModel (
    var tournID: String,
    var winnerTeamID: String,
    var name: String,
    var numbTeams: Int,
    var tournType: String,
) {
    companion object {
        const val TYPE_SINGLE_ELIM = "single elim"
        const val TYPE_DOUBLE_ELIM = "double elim"
        const val TYPE_ROUND_ROBIN = "round robin"
    }
}