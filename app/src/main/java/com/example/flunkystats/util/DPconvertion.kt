package com.example.flunkystats.util

import android.content.Context

object DPconvertion {

    fun Int.toDP(context: Context): Int {
        return (this*context.resources.displayMetrics.density).toInt()
    }

    fun Float.toDP(context: Context): Float {
        return this*context.resources.displayMetrics.density
    }

    fun toPix(dp: Int, context: Context): Int {
        return (dp/context.resources.displayMetrics.density).toInt()
    }

    fun toPix(dp: Float, context: Context): Float {
        return dp/context.resources.displayMetrics.density
    }
}