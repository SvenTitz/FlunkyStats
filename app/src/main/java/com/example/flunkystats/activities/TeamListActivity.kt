package com.example.flunkystats.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flunkystats.adapter.EntryListAdapter
import com.example.flunkystats.R
import com.example.flunkystats.models.ListEntryModel
import com.example.flunkystats.models.PlayerModel
import com.example.flunkystats.models.TeamModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_player_list.*
import kotlinx.android.synthetic.main.activity_team_list.*

class TeamListActivity : ListActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        viewManager = LinearLayoutManager(this)
        viewAdapter = EntryListAdapter(listDataset, "Spieler: ", this, TeamStatsActivity::class.java)

        updateDataset()

        findViewById<RecyclerView>(R.id.rv_TeamList).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //set on click listener for floating action button "add Player"
        findViewById<FloatingActionButton>(R.id.fab_add_team).setOnClickListener {
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
        viewAdapter.updateDataset(dbHelper.getTeamListData() ?: arrayListOf())
    }

    override fun addEntry(entryName: String) {
        fbDbHelper.addTeam(entryName) { teamID ->
            if (teamID.isNotEmpty()) {
                dbHelper.addTeam(TeamModel(teamID, entryName))
                viewAdapter.addEntry(ListEntryModel(entryName, teamID, null))
                Toast.makeText(this, "Added Team successfully", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to add team", Toast.LENGTH_LONG).show()
            }
        }
    }


}