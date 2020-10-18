package com.example.flunkystats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.flunkystats.data.*
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DataBaseHelper(this)
//        dbHelper.addPlayer(PlayerModel("1","Sven"))
//        dbHelper.addTeam(TeamModel("2", "Infos"))
//        dbHelper.addMatch(MatchModel("3", "4", "2"))
//        dbHelper.addTournament(TournamentModel("4", "2", "Tourny", 1, TournamentModel.TYPE_SINGLE_ELIM))
//        dbHelper.addPlayerTeamPair(PlayerTeamPairModel("5", "1", "2"))
//        dbHelper.addMatchPlayerPair(MatchPlayerPairModel("6", "3", "1", 5, 2, 3, true))
//        dbHelper.addMatchTeamPair(MatchTeamPairModel("7","3","2", 10, 3, 3, true))

        //set on click listener for Players Button
        findViewById<Button>(R.id.btnPlayers).setOnClickListener {
            startActivity(Intent(this, PlayerListActivity::class.java))
        }

        //set on click listener for Teams button
        findViewById<Button>(R.id.btnTeams).setOnClickListener {
            startActivity(Intent(this, TeamListActivity::class.java))
        }

        findViewById<Button>(R.id.btnMatches).setOnClickListener {
            val snack = Snackbar.make(it, "Not yet implemented", Snackbar.LENGTH_LONG)
            snack.show()
        }

        findViewById<Button>(R.id.btnTurnaments).setOnClickListener {
            val snack = Snackbar.make(it, "Not yet implemented", Snackbar.LENGTH_LONG)
            snack.show()
        }

    }
}