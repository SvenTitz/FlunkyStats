package com.example.flunkystats.util

import java.util.*

object StringUtil {

    /**
     * Capitalizes only the first character of every word in [s].
     * every other character is lower case.
     * " " (space) is used as a separator
     */
    fun capitalizeFirstLetters(s:String):String {
        return s.split(" ")
            .joinToString(" ") { it.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT) }
    }
}