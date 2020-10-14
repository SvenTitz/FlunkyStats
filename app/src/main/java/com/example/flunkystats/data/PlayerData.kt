@file:Suppress("unused")

package com.example.flunkystats.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class PlayerData(
    var playerID: String = "",
    var name: String = "",
    var teamID: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "playerID" to playerID,
            "name" to name,
            "teamID" to teamID
        )
    }

    override fun toString(): String {
        return "Player Name: $name, PlayerID: $playerID, TeamID: $teamID"
    }
}