package com.example.flunkystats

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Layout
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import androidx.core.view.setMargins
import kotlinx.android.synthetic.main.player_list.*

class PlayerListActivity: AppCompatActivity() {

    companion object {
        var btnIDs = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_list)

        loadPlayers()

        fabAddPlayer.setOnClickListener {
            val newButton = createNewButton("New Player"+PlayerListActivity.btnIDs++, llPlayerList)
        }
    }

    private fun createNewButton(buttonText: String, targetLayout: ViewGroup):Button {
        var newButton = Button(this)
        //set tag
        var buttonTag = "btn"+buttonText.replace("\\s".toRegex(), "")
        newButton.setTag("test")
        //set Text
        newButton.text = buttonText;
        //set Background
        newButton.setBackgroundResource(R.drawable.btn_primary_color)
        //add click listener
        newButton.setOnClickListener {
            Log.d("Sven", "button tag: ${newButton.getTag()}")
            startActivity(Intent(this, PlayerStatsActivity::class.java))
        }
        //add button to view group
        targetLayout.addView(newButton)
        //add margins
        var param = newButton.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(10,10,10,10)
        newButton.layoutParams = param

        return newButton
    }



    fun loadPlayers() {

    }

}