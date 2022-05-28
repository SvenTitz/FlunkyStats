package com.example.flunkystats.models

data class TeamModel(
    var teamID: String? = "",
    var teamName: String? = ""
) {

    override fun toString(): String {
        return "Team Name: $teamName, teamID : $teamID"
    }
}