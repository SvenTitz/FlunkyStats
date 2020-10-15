package com.example.flunkystats

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.flunkystats.util.StringUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

abstract class ListActivity: AppCompatActivity(), LoadsData {

    abstract val targetStatsActivity: Class<*>
    abstract val dataRef: DatabaseReference
    abstract val targetButtonLayout: ViewGroup


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
        //search for the entry
        val dataQ = dataRef.orderByKey().equalTo(entryID)
        //get the entry name from the database
        dataQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                val entry = values.iterator().next().value
                val name = entry.iterator().next().value
                //add a button for the entry to the entry list
                createNewButton(name, entryID, targetButtonLayout)
            }
            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })
    }

    /**
     * loads all entries when the activity is created
     */
    protected fun loadEntries() {

        //add progress bar while entries are loading
        val pgsBar = addProgressBar(findViewById(R.id.plaConstLayout), this)
        pgsBar.scaleX = 2F
        pgsBar.scaleY = pgsBar.scaleX
        pgsBar.visibility = View.VISIBLE

        //load all entry data
        dataRef.orderByChild("name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>

                //create map of entry IDs and names
                val entries = mutableMapOf<String, String>()

                //loop through all entries and add names and ids to map
                values.forEach { (k, v) ->
                    val name = v.iterator().next().value
                    entries[k] = name
                }

                //after all data has been loaded, add the buttons to the list
                pgsBar.visibility = View.GONE
                entries.forEach { (entryID, name ) ->
                    createNewButton(name, entryID, targetButtonLayout)
                }

            }

            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })


    }

    /**
     * Opens the Add Entry Alert Dialog
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
        //creates new entry with random key
        val newEntryID = dataRef.push().key

        if(newEntryID != null) {
            //add the entry to the database
            dataRef.child(newEntryID).child("name").setValue(name)
            loadSingleEntry(newEntryID)
        }
        else {
            Log.w("Sven", "Failed to push new entry")
        }
    }

    /**
     * Adds a new entry with name [name] to the database
     * if [checkDupName]: checks for duplicate names first
     */
    @Suppress ("SameParameterValue")
    private fun addEntry(name: String, checkDupName :Boolean) {

        //add entry without checking for duplicate names
        if( !checkDupName ) {
            addEntry(name)
            return
        }

        //search for entry with [name]
        val entryQ = dataRef.orderByChild("name").equalTo(name)
        entryQ.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.value == null) {
                    //no entry with this name found -> add entry
                    addEntry(name)
                }
                else {
                    //entry with duplicate name found -> open dialog and ask to add anyway
                    openDupNameDialog(name)
                }

            }

            override fun onCancelled (error: DatabaseError) {
                Log.w("Sven", "Failed to read value.", error.toException())
            }
        })

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