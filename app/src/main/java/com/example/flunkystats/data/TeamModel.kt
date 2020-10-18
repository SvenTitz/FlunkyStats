package com.example.flunkystats.data

data class TeamModel (
    var teamID: String = "",
    var name: String = ""
) {

    override fun toString(): String {
        return "Team Name: $name, teamID : $teamID"
    }
}