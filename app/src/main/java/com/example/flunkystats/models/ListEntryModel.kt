package com.example.flunkystats.models

data class ListEntryModel(
    val entryName: String,
    val entryID: String,
    val entryInfos: List<String>?
) {
    fun getInfoString(): String {
        var resString = ""
        if (entryInfos == null) return resString
        for (team in entryInfos) {
            resString += team
            if(team != entryInfos.last()) {
                resString += ", "
            }
        }
        return resString
    }
}