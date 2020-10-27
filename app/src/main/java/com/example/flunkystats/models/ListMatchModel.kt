package com.example.flunkystats.models

data class ListMatchModel (
    val matchID: String,
    val matchNumb: Int,
    val team1Name: String,
    val team1ID: String,
    val team2Name: String,
    val team2ID: String,
    val matchInfo: List<String>
)