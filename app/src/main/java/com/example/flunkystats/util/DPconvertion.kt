package com.example.flunkystats.util

import android.content.Context

object DPconvertion {

    fun toDP(x: Int, context: Context): Int {
        return (x*context.resources.displayMetrics.density).toInt()
    }

    fun toDP(x: Float, context: Context): Float {
        return x*context.resources.displayMetrics.density
    }
}