package com.example.flunkystats.models

@Suppress("unused")
data class TournamentModel (
    var tournID: String,
    var winnerTeamID: String?,
    var name: String?,
    var numbTeams: Int?,
    var tournType: String?,
) {
    companion object {
        const val TYPE_SINGLE_ELIM = "single_elim"
        const val TYPE_DOUBLE_ELIM = "double_elim"
        const val TYPE_ROUND_ROBIN = "round_robin"
    }
}