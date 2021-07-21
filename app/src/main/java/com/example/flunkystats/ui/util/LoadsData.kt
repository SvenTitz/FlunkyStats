package com.example.flunkystats.ui.util

import android.content.Context
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.flunkystats.R

/**
 * interface for all classes that load data from the database
 */
interface LoadsData {

    /**
     * Adds a progressbar to the center of the constraint layout [layout] in [context]
     */
    fun addProgressBar (layout: ConstraintLayout, context: Context): ProgressBar {
        //inflate progressbar and add it to layout
        val pgsBar = ProgressBar.inflate(context, R.layout.inflatable_progress_bar, null) as ProgressBar
        layout.addView(pgsBar)

        //center it
        val constSet = ConstraintSet()

        constSet.constrainHeight(pgsBar.id, ConstraintSet.WRAP_CONTENT)
        constSet.constrainWidth(pgsBar.id, ConstraintSet.WRAP_CONTENT)

        constSet.connect(pgsBar.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
        constSet.connect(pgsBar.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
        constSet.connect(pgsBar.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constSet.connect(pgsBar.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

        constSet.applyTo(layout)

        return pgsBar
    }

    /**
     * adds a progressbar to [layout] in [context]. no additional positioning of progressbar
     */
    fun addProgressBar (layout: ViewGroup, context: Context): ProgressBar {
        //inflate progressbar and add it to layout
        val pgsBar = ProgressBar.inflate(context, R.layout.inflatable_progress_bar, null) as ProgressBar
        layout.addView(pgsBar)

        return pgsBar
    }
}