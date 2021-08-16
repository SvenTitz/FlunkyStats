package com.example.flunkystats.activities

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flunkystats.AppConfig.Companion.TAG
import com.example.flunkystats.adapter.EntryListAdapter
import com.example.flunkystats.R
import com.example.flunkystats.models.ListEntryModel
import com.example.flunkystats.models.PlayerModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_player_list.*

class PlayerListActivity: ListActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        viewManager = LinearLayoutManager(this)
        viewAdapter = EntryListAdapter(listDataset, "Teams: ", this, PlayerStatsActivity::class.java)

        updateDataset()

        findViewById<RecyclerView>(R.id.rv_PlayerList).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //set on click listener for floating action button "add Player"
        findViewById<FloatingActionButton>(R.id.fab_add_player).setOnClickListener {
            //open the add player alert dialog
            fbDbHelper.testAuth {
                if (it) {
                    //Toast.makeText(this, "You ARE authorized to edit the database", Toast.LENGTH_LONG)
                    openAddEntryAlertDialog()
                } else {
                    val toast =  Toast.makeText(this, "You are NOT authorized to edit the database", Toast.LENGTH_LONG)
                    toast.show()
                }

            }

        }
    }

    override fun updateDataset() {
        viewAdapter.updateDataset(dbHelper.getPlayerListData() ?: arrayListOf())
    }


    override fun addEntry(entryName: String) {
        fbDbHelper.addPlayer(entryName) { playerID ->
            if (playerID.isNotEmpty()) {
                dbHelper.addPlayer(PlayerModel(playerID, entryName))
                viewAdapter.addEntry(ListEntryModel(entryName, playerID, null))
                Toast.makeText(this, "Added Player successfully", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to add player", Toast.LENGTH_LONG).show()
            }
        }
    }

}