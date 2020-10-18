package com.example.flunkystats.data

data class PlayerModel(
    var playerID: String = "",
    var name: String = "",
) {

    override fun toString(): String {
        return "Player Name: $name, PlayerID: $playerID"
    }
}