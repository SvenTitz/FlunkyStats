package com.example.flunkystats

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flunkystats.database.DataBaseHelper
import com.example.flunkystats.models.FilterListItemModel


abstract class StatsActivity: AppCompatActivity() {

    lateinit var dbHelper: DataBaseHelper

    protected lateinit var tournFilterData: ArrayList<FilterListItemModel>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DataBaseHelper(this)
        tournFilterData = loadTournFilterData()
    }

    /**
     * Adds a TextView to [targetLayout] with [name] as Text and font size [textSize]
     * default [textSize] is 36dp
     * Returns the new TextView
     */
    protected fun createTextView(name: String, entryID :String, targetLayout: ConstraintLayout, prevView: TextView?, textSize: Float = 36f): TextView {
        val newTV:TextView = TextView.inflate(this, R.layout.inflatable_stats_single_text, null) as TextView

        newTV.text = name
        newTV.tag = entryID
        newTV.textSize = textSize
        newTV.id = entryID.hashCode()

        targetLayout.addView(newTV)

        val constSet = ConstraintSet()

        constSet.constrainHeight(newTV.id, ConstraintSet.WRAP_CONTENT)
        constSet.constrainWidth(newTV.id, 0)

        constSet.connect(newTV.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dp(32))
        constSet.connect(newTV.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dp(32))
        if( prevView == null) {
            constSet.connect(newTV.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, dp(16))
        } else {
            constSet.connect(newTV.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, dp(8))
        }

        constSet.applyTo(targetLayout)

        if(prevView != null) {
            addVertDivider(targetLayout, prevView, newTV)
        }


        return newTV
    }

    private fun addVertDivider(targetLayout: ConstraintLayout, topView: View, botView: View): View {
        val divider = View.inflate(this, R.layout.inflatable_stats_vertical_divier, null)

        divider.id = View.generateViewId()

        targetLayout.addView(divider)

        val constSet = ConstraintSet()

        constSet.constrainHeight(divider.id, dp(1))
        constSet.constrainWidth(divider.id, 0)

        constSet.connect(divider.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dp(32))
        constSet.connect(divider.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dp(32))
        constSet.connect(divider.id, ConstraintSet.TOP, topView.id, ConstraintSet.BOTTOM)
        constSet.connect(divider.id, ConstraintSet.BOTTOM, botView.id, ConstraintSet.TOP)

        constSet.applyTo(targetLayout)

        return divider
    }

    private fun dp(x: Int): Int {
        return (x*this.resources.displayMetrics.density).toInt()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_stats, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_stats_filter -> {
                openFilterAlertDialog(item)
            }
        }

        return true
    }

    protected abstract fun openFilterAlertDialog(item: MenuItem)

    protected abstract fun loadTournFilterData(): ArrayList<FilterListItemModel>

    protected fun buildFilterRecView(recViewID: Int, dataset: ArrayList<FilterListItemModel>, view: View) {
        val viewManager = LinearLayoutManager(this)
        val viewAdapter = FilterListAdapter(dataset, this)

        view.findViewById<RecyclerView>(recViewID).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}