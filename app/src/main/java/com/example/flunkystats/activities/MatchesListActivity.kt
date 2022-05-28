package com.example.flunkystats.activities

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flunkystats.AppConfig.Companion.TAG
import com.example.flunkystats.R
import com.example.flunkystats.adapter.LinkedHashMapAdapter
import com.example.flunkystats.adapter.MatchListAdapter
import com.example.flunkystats.database.DataBaseHelper
import com.example.flunkystats.database.FirebaseDatabaseHelper
import com.example.flunkystats.models.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.lang.Exception

class MatchesListActivity : AppCompatActivity() {

    lateinit var dbHelper: DataBaseHelper
    lateinit var fbDbHelper: FirebaseDatabaseHelper
    lateinit var viewManager: LinearLayoutManager
    lateinit var viewAdapter: MatchListAdapter
    private lateinit var listDataset: ArrayList<ListMatchModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matches_list)

        dbHelper = DataBaseHelper(this)
        fbDbHelper = FirebaseDatabaseHelper(dbHelper)
        listDataset = arrayListOf()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //val listDataset = dbHelper.getMatchListData() ?: arrayListOf()

        viewManager = LinearLayoutManager(this)
        viewAdapter = MatchListAdapter(listDataset, this)

        updateDataset()

        findViewById<RecyclerView>(R.id.rv_MatchesList).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        findViewById<FloatingActionButton>(R.id.fab_add_match).setOnClickListener {
            fbDbHelper.testAuth {
                if (it) {
                    openAddMatchAlertDialog()

                } else {
                    val toast = Toast.makeText(this, "You are NOT authorized to edit the database", Toast.LENGTH_LONG)
                    toast.show()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        updateDataset()
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

    private fun updateDataset() {
        viewAdapter.updateDataset(dbHelper.getMatchListData())
    }

    private fun openAddMatchAlertDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogStyle)
        builder.setTitle("Spiel Hinzufügen")

        val view: View = ConstraintLayout.inflate(this, R.layout.inflatable_dialog_add_m, null)

        builder.setView(view)

        val dialog = builder.create()

        val tournMapData = linkedMapOf<String, String?>()
        val tournListData = dbHelper.getTournListData()
        tournListData.forEach {
            tournMapData[it.tournID] = it.name
        }
        val adapterTourn = LinkedHashMapAdapter<String, String>(this, R.layout.spinner_item_big, tournMapData)
        adapterTourn.setDropDownViewResource(R.layout.spinner_item_big)

        val spinnerTourn = view.findViewById<Spinner>(R.id.sp_dialog_add_m_tourn)
        spinnerTourn.adapter = adapterTourn

        val teamMapData = linkedMapOf<String, String>()
        val teamListData = dbHelper.getTeamListData()
        teamListData.forEach {
            teamMapData[it.entryID] = it.entryName
        }
        val adapterTeam1 = LinkedHashMapAdapter<String, String>(this, R.layout.spinner_item_big, teamMapData)
        adapterTeam1.setDropDownViewResource(R.layout.spinner_item_big)
        val adapterTeam2 = LinkedHashMapAdapter<String, String>(this, R.layout.spinner_item_big, teamMapData)
        adapterTeam2.setDropDownViewResource(R.layout.spinner_item_big)

        val spinnerTeam1 = view.findViewById<Spinner>(R.id.sp_dialog_add_m_team1)
        spinnerTeam1.adapter = adapterTeam1
        val spinnerTeam2 = view.findViewById<Spinner>(R.id.sp_dialog_add_m_team2)
        spinnerTeam2.adapter = adapterTeam2

        val etnMatchNumb = view.findViewById<EditText>(R.id.etn_dialog_add_m)


        view.findViewById<Button>(R.id.btn_dialog_cacel).setOnClickListener {
            Handler().postDelayed({
                dialog.cancel()
            }, 150)
        }

        view.findViewById<Button>(R.id.btn_dialog_ok).setOnClickListener {
            if (spinnerTourn.selectedItem != null && spinnerTeam1.selectedItem != null && spinnerTeam2.selectedItem != null) {
                val tourn = spinnerTourn.selectedItem as Map.Entry<*, *>
                val team1 = spinnerTeam1.selectedItem as Map.Entry<*, *>
                val team2 = spinnerTeam2.selectedItem as Map.Entry<*, *>
                tryAddMatch(tourn.key.toString(), etnMatchNumb.text.toString().toIntOrNull(), team1.key.toString(), team2.key.toString())
            }
            Handler().postDelayed({
                dialog.cancel()
            }, 150)
        }

        dialog.show()
    }

    private fun tryAddMatch(tournID: String, matchNumb: Int?, team1ID: String, team2ID: String) {
        //check for valid match numb
        if(matchNumb == null) {
            val toast = Toast.makeText(this, "Keine gültige Spielnummer ausgewählt. Spiel NICHT hinzugefügt.", Toast.LENGTH_LONG)
            toast.show()
            return
        }
        //check if match already exists
        //in db
        if (dbHelper.matchExists(tournID, matchNumb)) {
            //spiel existiert bereits lokal, mache nichts
            val toast = Toast.makeText(this, "Das Spiel existiert bereits in der lokalen Datenbank. Spiel NICHT hinzugefügt.", Toast.LENGTH_LONG)
            toast.show()
            return
        }
        //in fb
        fbDbHelper.matchExists(tournID, matchNumb) { exists ->
            if (exists) {
                //spiel existiert berteits online. mache nichts
                val toast = Toast.makeText(this, "Das Spiel existiert bereits in der online Datenbank. Spiel NICHT hinzugefügt.", Toast.LENGTH_LONG)
                toast.show()
            } else {
                //spiel existiert weder lokal noch online -> füge spiel hinzu
                addMatch(tournID, matchNumb, team1ID, team2ID)
            }
        }
    }

    private fun addMatch(tournID: String, matchNumb: Int, team1ID: String, team2ID: String) {
        val playersTeam1 = dbHelper.getTeamsPlayers(team1ID)
        val playersTeam2 = dbHelper.getTeamsPlayers(team2ID)

        if (team1ID == team2ID) {
            val toast = Toast.makeText(this, "Es wurden 2 mal das gleiche Team ausgesucht. Spiel NICHT hinzugefügt.", Toast.LENGTH_LONG)
            toast.show()
            return
        } else if (playersTeam1.size != 2) {
            val toast = Toast.makeText(this, "Team 1 hat nicht genau zwei Spieler. Spiel NICHT hinzugefügt.", Toast.LENGTH_LONG)
            toast.show()
            return
        } else if (playersTeam2.size != 2) {
            val toast = Toast.makeText(this, "Team 2 hat nicht genau zwei Spieler. Spiel NICHT hinzugefügt.", Toast.LENGTH_LONG)
            toast.show()
            return
        }

        //add match
        fbDbHelper.addMatch(tournID, matchNumb) { matchID ->
            Log.d(TAG, "Match added ID: $matchID")
            dbHelper.addMatch(MatchModel(matchID, tournID, matchNumb))

            //add match team pairs
            fbDbHelper.addMatchTeamPair(matchID, team1ID, false) {
                dbHelper.addMatchTeamPair(MatchTeamPairModel(it, matchID = matchID, teamID = team1ID, won = false))
            }
            fbDbHelper.addMatchTeamPair(matchID, team2ID, false) {
                dbHelper.addMatchTeamPair(MatchTeamPairModel(it, matchID = matchID, teamID = team2ID, won = false))
            }

            //add match player pairs
            fbDbHelper.addMatchPlayerPair(matchID, playersTeam1[0].playerID!!, 0, 0, 0, false) {
                dbHelper.addMatchPlayerPair(MatchPlayerPairModel(it, matchID, playersTeam1[0].playerID, 0, 0, 0, false))
            }
            fbDbHelper.addMatchPlayerPair(matchID, playersTeam1[1].playerID!!, 0, 0, 0, false) {
                dbHelper.addMatchPlayerPair(MatchPlayerPairModel(it, matchID, playersTeam1[1].playerID, 0, 0, 0, false))
            }
            fbDbHelper.addMatchPlayerPair(matchID, playersTeam2[0].playerID!!, 0, 0, 0, false) {
                dbHelper.addMatchPlayerPair(MatchPlayerPairModel(it, matchID, playersTeam2[0].playerID, 0, 0, 0, false))
            }
            fbDbHelper.addMatchPlayerPair(matchID, playersTeam2[1].playerID!!, 0, 0, 0, false) {
                dbHelper.addMatchPlayerPair(MatchPlayerPairModel(it, matchID, playersTeam2[1].playerID, 0, 0, 0, false))
            }

            val listMatchModel = ListMatchModel(
                matchID = matchID,
                matchNumb = matchNumb,
                team1Name = dbHelper.getTeamName(team1ID),
                team1ID = team1ID,
                team2Name = dbHelper.getTeamName(team2ID),
                team2ID = team2ID,
                winnerID = null,
                matchInfo = listOf(dbHelper.getTournName(tournID))
            )
            viewAdapter.addEntry(listMatchModel)
            //man kann nicht sofort zur match stats seite, da zu diesem zeitpunkt die relevanten sachen noch nicht in der db sind
//            val intent = Intent(applicationContext, MatchStatsActivity::class.java).apply {
//                putExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID, matchID)
//            }
//            applicationContext.startActivity(intent)
        }
    }

}