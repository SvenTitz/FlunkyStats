package com.example.flunkystats.models

data class PlayerModel(
    var playerID: String? = "",
    var playerName: String? = "",
) {

    override fun toString(): String {
        return "Player Name: $playerName, PlayerID: $playerID"
    }
}