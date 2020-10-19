package com.example.flunkystats.database.models

data class TimestampsModel(
    val playersTS: Long?,
    val teamsTS: Long?,
    val matchesTS: Long?,
    val tournTS: Long?,
    val playerTeamTS: Long?,
    val matchPlayerTS: Long?,
    val matchTeamTS: Long?
)