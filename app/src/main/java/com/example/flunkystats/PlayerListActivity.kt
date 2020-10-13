package com.example.flunkystats

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_player_list.*

class PlayerListActivity: AppCompatActivity() {

    companion object {
        var btnIDs = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_list)

        loadPlayers()

        fabAddPlayer.setOnClickListener {
//            startActivity(Intent(this, PlayerCreatorActivity::class.java))
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
            builder.setTitle("Spieler Hinzufügen:")
//            val edittext = EditText(this)
            val edittext:EditText = EditText.inflate(this, R.layout.inflatable_edit_text, null) as EditText

//            <EditText
//            android:id="@+id/etPlayerName"
//            android:layout_width="match_parent"
//            android:layout_height="wrap_content"
//            android:ems="10"
//            android:hint="Spieler Name"
//            android:textColor="@color/colorPrimary"
//            android:backgroundTint="@color/backgroundDark"
//            android:textColorHint="@color/backgroundDark"
//            android:textSize="30sp" />

            edittext.hint = "Spieler Name"

            builder.setView(edittext)
            val dialog :AlertDialog = builder.show()
        }
    }

    private fun createNewButton(playerName: String, playerID: String, targetLayout: ViewGroup):Button {
        var newBtn = Button(this)
        //set tag
        newBtn.tag = playerID
        //set Text
        newBtn.text = playerName;
        //set Background
        newBtn.setBackgroundResource(R.drawable.btn_primary_color)
        //add click listener
        newBtn.setOnClickListener {
            Log.d("Sven", "button tag: ${newBtn.tag.toString()}")
            val intent = Intent(this, PlayerStatsActivity::class.java).apply {
                putExtra(AppConfig.EXTRA_MESSAGE_PLAYER_ID, newBtn.tag.toString())
            }
            startActivity(intent)
        }
        //add button to view group
        targetLayout.addView(newBtn)
        //add margins
        var param = newBtn.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(10,10,10,10)
        newBtn.layoutParams = param

        return newBtn
    }

    fun loadPlayers() {
        val database = Firebase.database
        val playerRef = database.reference.child("Players")
         playerRef.addListenerForSingleValueEvent(object : ValueEventListener {
             @RequiresApi(Build.VERSION_CODES.N)
             override fun onDataChange(dataSnapshot: DataSnapshot) {
                 val values = dataSnapshot.value as HashMap<String, HashMap<String, String>>
                 Log.d("Sven", "values: $values")
                 values.forEach { (k, v) ->
                     Log.d("Sven", "Entry: Key $k, Value: $v")
                     val name = v.iterator().next().value
                     createNewButton(name, k, llPlayerList)
                 }
             }
             override fun onCancelled (error: DatabaseError) {
                 Log.w("Sven", "Failed to read value.", error.toException())
             }
         })


    }

}