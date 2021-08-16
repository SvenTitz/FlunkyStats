package com.example.flunkystats.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flunkystats.AppConfig.Companion.TAG
import com.example.flunkystats.adapter.FilterListAdapter
import com.example.flunkystats.R
import com.example.flunkystats.database.DataBaseHelper
import com.example.flunkystats.database.FirebaseDatabaseHelper
import com.example.flunkystats.models.FilterListItemModel
import com.example.flunkystats.models.ListEntryModel
import com.example.flunkystats.models.PlayerModel
import com.example.flunkystats.ui.main.SimpleDividerItemDecoration
import com.example.flunkystats.util.DPconvertion
import com.example.flunkystats.util.DPconvertion.toDP


abstract class StatsActivity: AppCompatActivity() {

    lateinit var dbHelper: DataBaseHelper
    lateinit var fbDbHelper: FirebaseDatabaseHelper

    protected lateinit var tournFilterData: ArrayList<FilterListItemModel>
    protected lateinit var entryName: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DataBaseHelper(this)
        fbDbHelper = FirebaseDatabaseHelper(dbHelper)
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

        constSet.connect(newTV.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dp(32F))
        constSet.connect(newTV.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dp(32F))
        if( prevView == null) {
            constSet.connect(newTV.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, dp(16F))
        } else {
            constSet.connect(newTV.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, dp(8F))
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

        constSet.constrainHeight(divider.id, dp(1F))
        constSet.constrainWidth(divider.id, 0)

        constSet.connect(divider.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, dp(32F))
        constSet.connect(divider.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, dp(32F))
        constSet.connect(divider.id, ConstraintSet.TOP, topView.id, ConstraintSet.BOTTOM)
        constSet.connect(divider.id, ConstraintSet.BOTTOM, botView.id, ConstraintSet.TOP)

        constSet.applyTo(targetLayout)

        divider.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))

        return divider
    }

    private fun dp(x: Float): Int {
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
            R.id.menu_stats_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.menu_stats_delete -> {
                openDeleteEntryDialog()
            }
            R.id.menu_stats_edit -> {
                openEditEntryNameDialog()
            }
        }

        return true
    }

    protected abstract fun editEntry(name: String)

    protected abstract fun deleteEntry()

    protected abstract fun openFilterAlertDialog(item: MenuItem)

    protected abstract fun loadTournFilterData(): ArrayList<FilterListItemModel>

    protected fun buildFilterRecView(recViewID: Int, dataset: ArrayList<FilterListItemModel>, view: View): FilterListAdapter {
        val viewManager = LinearLayoutManager(this)
        val viewAdapter = FilterListAdapter(dataset, this)

        view.findViewById<RecyclerView>(recViewID).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            addItemDecoration(SimpleDividerItemDecoration(context, 40F.toDP(context), 40F.toDP(context)))
        }

        return viewAdapter
    }

    private fun openDeleteEntryDialog() {
        fbDbHelper.testAuth {
            if (it) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
                builder.setTitle("Eintrag Löschen")
                builder.setMessage("Bist du sicher, dass du den Eintrag löschen willst?")
                builder.setPositiveButton("Löschen") {dialog, _ ->
                    deleteEntry()
                    Handler().postDelayed( {
                        dialog.cancel()
                    }, 150)
                }
                builder.setNegativeButton("Abbrechen") { dialog, _ ->
                    Handler().postDelayed( {
                        dialog.cancel()
                    }, 150)
                }

                builder.create().show()
            } else {
                val toast =  Toast.makeText(this, "You are NOT authorized to edit the database", Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }

    private fun openEditEntryNameDialog() {
        fbDbHelper.testAuth {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
            builder.setTitle("Name Bearbeiten:")

            val view: View = ConstraintLayout.inflate(this, R.layout.inflatable_dialog_add_p, null)

            builder.setView(view)

            val dialog = builder.create()

            val etName: EditText = view.findViewById<EditText>(R.id.et_add_dialog_p)

            etName.setText(entryName)

            view.findViewById<Button>(R.id.btn_add_dialog_cacel).setOnClickListener {
                Handler().postDelayed( {
                    dialog.cancel()
                }, 150)
            }

            view.findViewById<Button>(R.id.btn_add_dialog_ok).setOnClickListener {
                editEntry(etName.text.toString())
                Handler().postDelayed( {
                    dialog.cancel()
                }, 150)
            }

            dialog.show()
        }
    }
}