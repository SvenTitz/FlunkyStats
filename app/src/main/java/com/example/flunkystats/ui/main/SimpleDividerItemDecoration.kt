package com.example.flunkystats.ui.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.flunkystats.R


class SimpleDividerItemDecoration(context: Context, private val paddingLeft: Float, private val paddingRight: Float) : ItemDecoration() {

    private val mDivider: Drawable

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = paddingLeft.toInt()
        val right = parent.width - paddingRight.toInt()
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top: Int = child.bottom + params.bottomMargin
            val bottom = top + mDivider.intrinsicHeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }


    init {
        mDivider = ContextCompat.getDrawable(context, R.drawable.line_divider)!!
    }
}