package com.example.flunkystats.models

@Suppress("unused")
data class TournamentModel (
    var tournID: String,
    var winnerTeamID: String? = null,
    var name: String? = null,
    var numbTeams: Int? = null,
    var tournType: String? = null,
) {
    companion object {
        const val TYPE_SINGLE_ELIM = "single_elim"
        const val TYPE_DOUBLE_ELIM = "double_elim"
        const val TYPE_ROUND_ROBIN = "round_robin"
    }
}