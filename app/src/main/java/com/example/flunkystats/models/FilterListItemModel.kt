package com.example.flunkystats.models

data class FilterListItemModel (
    val id: String,
    val name: String,
    var checked: Boolean = true
)