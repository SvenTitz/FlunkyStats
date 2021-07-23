package com.example.flunkystats.activities


import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flunkystats.adapter.EntryListAdapter
import com.example.flunkystats.R
import com.example.flunkystats.database.DataBaseHelper
import com.example.flunkystats.database.FirebaseDatabaseHelper

abstract class ListActivity: AppCompatActivity() {

    lateinit var dbHelper: DataBaseHelper
    lateinit var fbDbHelper: FirebaseDatabaseHelper
    lateinit var viewManager: LinearLayoutManager
    lateinit var viewAdapter: EntryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DataBaseHelper(this)
        fbDbHelper = FirebaseDatabaseHelper(dbHelper)
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