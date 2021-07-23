package com.example.flunkystats.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flunkystats.adapter.EntryListAdapter
import com.example.flunkystats.R
import kotlinx.android.synthetic.main.activity_player_list.*

class PlayerListActivity: ListActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val listDataset = dbHelper.getPlayerListData() ?: arrayListOf()

        viewManager = LinearLayoutManager(this)
        viewAdapter = EntryListAdapter(listDataset, "Teams: ", this, PlayerStatsActivity::class.java)

        findViewById<RecyclerView>(R.id.rv_PlayerList).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //set on click listener for floating action button "add Player"
        fab_add_player.setOnClickListener {
            //open the add player alert dialog
            fbDbHelper.testAuth {
                val toast = if (it) {
                    Toast.makeText(this, "You ARE authorized to edit the database", Toast.LENGTH_LONG)
                } else {
                    Toast.makeText(this, "You are NOT authorized to edit the database", Toast.LENGTH_LONG)
                }
                toast.show()
            }

        }
    }

}