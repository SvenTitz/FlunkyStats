package com.example.flunkystats.data

import java.net.IDN

data class TeamData (
    var member1ID: String = "",
    var member2ID: String = "",
    var name: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "member1ID" to member1ID,
            "member2ID" to member2ID,
            "name" to name
        )
    }

    override fun toString(): String {
        return "Team Name: $name, Member1ID: $member1ID, Member2ID: $member2ID"
    }
}