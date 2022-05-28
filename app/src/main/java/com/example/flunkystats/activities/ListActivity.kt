package com.example.flunkystats.activities


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flunkystats.R
import com.example.flunkystats.adapter.EntryListAdapter
import com.example.flunkystats.database.DataBaseHelper
import com.example.flunkystats.database.FirebaseDatabaseHelper
import com.example.flunkystats.models.ListEntryModel

abstract class ListActivity: AppCompatActivity() {

    lateinit var dbHelper: DataBaseHelper
    lateinit var fbDbHelper: FirebaseDatabaseHelper
    lateinit var viewManager: LinearLayoutManager
    lateinit var viewAdapter: EntryListAdapter
    lateinit var listDataset: ArrayList<ListEntryModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DataBaseHelper(this)
        fbDbHelper = FirebaseDatabaseHelper(dbHelper)
        listDataset = arrayListOf()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_list_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        updateDataset()
    }

    protected fun openAddEntryAlertDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
        builder.setTitle("Eintrag Hinzufügen:")

        val view: View = ConstraintLayout.inflate(this, R.layout.inflatable_dialog_add_p, null)

        builder.setView(view)

        val dialog = builder.create()

        val etName: EditText = view.findViewById<EditText>(R.id.et_add_dialog_p)

        view.findViewById<Button>(R.id.btn_add_dialog_cacel).setOnClickListener {
            Handler().postDelayed( {
                dialog.cancel()
            }, 150)
        }

        view.findViewById<Button>(R.id.btn_add_dialog_ok).setOnClickListener {
            val entryName = etName.text.toString()
            addEntry(entryName)
            Handler().postDelayed( {
                dialog.cancel()
            }, 150)
        }

        dialog.show()
    }




    protected abstract fun updateDataset()

    protected abstract fun addEntry(entryName: String)

}