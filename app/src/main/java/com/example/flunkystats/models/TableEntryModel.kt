package com.example.flunkystats.models

data class TableEntryModel(
    val name: String,
    val stat1: String,
    val stat2: String?,
    val stat3: String?
) {
    fun compareTo(other: TableEntryModel, stat: Int): Int {
        when(stat) {
            1 -> {
                var otherStat = other.stat1
                var ownStat = stat1
                if (ownStat.last() == '%') ownStat = ownStat.dropLast(1)
                if (otherStat.last() == '%') otherStat = otherStat.dropLast(1)
                return ownStat.toFloat().compareTo(otherStat.toFloat())
            }
            2 -> {
                var otherStat = other.stat2
                var ownStat = stat2
                if (ownStat == null || otherStat == null) return 0
                if (ownStat.last() == '%') ownStat = ownStat.dropLast(1)
                if (otherStat.last() == '%') otherStat = otherStat.dropLast(1)
                return ownStat.toFloat().compareTo(otherStat.toFloat())
            }
            3 -> {
                var otherStat = other.stat3
                var ownStat = stat3
                if (ownStat == null || otherStat == null) return 0
                if (ownStat.last() == '%') ownStat = ownStat.dropLast(1)
                if (otherStat.last() == '%') otherStat = otherStat.dropLast(1)
                return ownStat.toFloat().compareTo(otherStat.toFloat())
            }
            else -> return 0
        }

    }


}