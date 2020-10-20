package com.example.flunkystats

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flunkystats.database.DataBaseHelper
import com.example.flunkystats.util.StringUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

abstract class ListActivity: AppCompatActivity(), LoadsData {

    abstract val targetStatsActivity: Class<*>
    abstract val targetButtonLayout: ViewGroup
    abstract val rootLayout: ConstraintLayout
    lateinit var dbHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DataBaseHelper(this)
    }

    /**
     * creates a new button for the entry of name [btnText] and ID [entryID] and adds it to layout [targetLayout]
     */
    private fun createNewButton(btnText: String, entryID: String, targetLayout: ViewGroup):Button {
        //create the new button
        val newBtn: Button = Button.inflate(this,  R.layout.inflatable_button, null) as Button
        newBtn.text = btnText
        //set entry ID as tag
        newBtn.tag = entryID
        //add button to target view group
        targetLayout.addView(newBtn)
        //add margins
        val layoutParams = newBtn.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(16,16,16,16)
        newBtn.layoutParams = layoutParams


        //add click listener
        newBtn.setOnClickListener {
            //Open stats page of the entry. send entryID as extra message
            val intent = Intent(this, targetStatsActivity).apply {
                putExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID, newBtn.tag.toString())
            }
            startActivity(intent)
        }

        return newBtn
    }

    /**
     * loads a single entry with ID [entryID] from the database.
     * This method is called after a new entry is added with the floating action button
     */
    private fun loadSingleEntry(entryID: String) {

    }

    /**
     * loads all entries when the activity is created
     */
    protected fun loadEntries(tableName: String) {
        val idNameMap = dbHelper.getIDandName(tableName)

        idNameMap.forEach { (id, name) ->
            createNewButton(name, id, targetButtonLayout)
        }
    }

    /**
     * Opens the Add Entry Alert Dialog with [title] and [hint]
     */
    protected fun openAddEntryDialog(title: String, hint: String) {
        //create new alert dialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
        builder.setTitle(title)

        //add the EditText for the entry Name to the Dialog
        val edittext:EditText = EditText.inflate(this, R.layout.inflatable_edit_text, null) as EditText
        edittext.hint = hint
        builder.setView(edittext)

        //add functionality to positive Button
        builder.setPositiveButton("Hinzufügen") { dialog, _ ->
            //add a new entry to database
            val entryName:String = StringUtil.capitalizeFirstLetters(edittext.text.toString())
            addEntry(entryName, true)
            dialog.cancel()
        }

        //add functionality to negative Button
        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            dialog.cancel()
        }

        //open the dialog
        builder.show()
    }

    /**
     * Adds a new entry with name [name] to the database
     */
    private fun addEntry(name: String) {

    }

    /**
     * Adds a new entry with name [name] to the database
     * if [checkDupName]: checks for duplicate names first
     */
    private fun addEntry(name: String, checkDupName :Boolean) {


    }

    /**
     * used when trying to add a entry with duplicate [name] to the database
     * give option to add entry anyway or cancel
     */
    private fun openDupNameDialog(name: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
        builder.setTitle("Eintrag mit diesem Namen existiert bereits.")
        builder.setMessage("Trotzdem hinzufügen?")

        //add functionality to positive Button
        builder.setPositiveButton("Hinzufügen") { dialog, _ ->
            addEntry(name)
            dialog.cancel()
        }

        //add functionality to negative Button
        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

}