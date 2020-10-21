package com.example.flunkystats.models

data class PlayerModel(
    var playerID: String = "",
    var name: String? = "",
) {

    override fun toString(): String {
        return "Player Name: $name, PlayerID: $playerID"
    }
}