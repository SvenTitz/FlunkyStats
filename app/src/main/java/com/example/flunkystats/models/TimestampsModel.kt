package com.example.flunkystats.models

data class TimestampsModel(
    val playersTS: Long? = null,
    val teamsTS: Long? = null,
    val matchesTS: Long? = null,
    val tournTS: Long? = null,
    val playerTeamTS: Long? = null,
    val matchPlayerTS: Long? = null,
    val matchTeamTS: Long? = null
)