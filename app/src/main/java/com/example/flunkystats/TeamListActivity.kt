package com.example.flunkystats

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_team_list.*

class TeamListActivity : ListActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val listDataset = dbHelper.getTeamListData() ?: arrayListOf()

        viewManager = LinearLayoutManager(this)
        viewAdapter = ListAdapter(listDataset, "Spieler: ", this, TeamStatsActivity::class.java)

        findViewById<RecyclerView>(R.id.rv_TeamList).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //set on click listener for floating action button "add Player"
        fabAddTeam.setOnClickListener {
            //open the add player alert dialog
            Toast.makeText(this, "disabled for now", Toast.LENGTH_LONG).show()
//            openAddEntryDialog(getString(R.string.addTeamDialogTitle), getString(R.string.addTeamDialogHint))
        }

    }


}