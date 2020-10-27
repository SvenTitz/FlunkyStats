package com.example.flunkystats.activities

import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flunkystats.R
import com.example.flunkystats.adapter.EntryListAdapter
import com.example.flunkystats.adapter.MatchListAdapter
import com.example.flunkystats.database.DataBaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_team_list.*

class MatchesListActivity : AppCompatActivity() {

    lateinit var dbHelper: DataBaseHelper
    lateinit var viewManager: LinearLayoutManager
    lateinit var viewAdapter: MatchListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matches_list)

        dbHelper = DataBaseHelper(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val listDataset = dbHelper.getMatchListData() ?: arrayListOf()

        viewManager = LinearLayoutManager(this)
        viewAdapter = MatchListAdapter(listDataset, this)

        findViewById<RecyclerView>(R.id.rv_MatchesList).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)

        val searchItem = menu?.findItem(R.id.menu_list_search) ?: return true
        val searchView = searchItem.actionView as SearchView

        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                viewAdapter.filter.filter(qString)
                return false
            }
            override fun onQueryTextSubmit(qString: String): Boolean {
                return false
            }
        })
        return true
    }


}