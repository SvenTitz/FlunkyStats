package com.example.flunkystats

import android.content.Context
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

interface LoadsData {

    fun addProgressBar (layout: ConstraintLayout, context: Context): ProgressBar {
        val pgsBar = ProgressBar.inflate(context, R.layout.inflatable_progress_bar, null) as ProgressBar
        layout.addView(pgsBar)

        var constSet = ConstraintSet()

        constSet.constrainHeight(pgsBar.id, ConstraintSet.WRAP_CONTENT)
        constSet.constrainWidth(pgsBar.id, ConstraintSet.WRAP_CONTENT)

        constSet.connect(pgsBar.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
        constSet.connect(pgsBar.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
        constSet.connect(pgsBar.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constSet.connect(pgsBar.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

        constSet.applyTo(layout)

        return pgsBar
    }
}