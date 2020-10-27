package com.example.flunkystats.ui.main

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckedTextView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flunkystats.AppConfig
import com.example.flunkystats.LoadsData
import com.example.flunkystats.R
import com.example.flunkystats.adapter.FilterListAdapter
import com.example.flunkystats.adapter.TableListAdapter
import com.example.flunkystats.database.DataBaseHelper
import com.example.flunkystats.models.EntryModel
import com.example.flunkystats.models.FilterListItemModel
import com.example.flunkystats.models.TableEntryModel
import com.example.flunkystats.util.DPconvertion
import java.lang.IllegalArgumentException
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList


// the fragment initialization parameters
private const val ARG_STAT_TYPE = "statType"

class RankingFragment : Fragment(), LoadsData {

    private var statType: Int? = null
    private var dataList: ArrayList<TableEntryModel> = arrayListOf()
    private var entryType: Int = ENTRY_PLAYERS

    private lateinit var dbHelper: DataBaseHelper
    private lateinit var tableAdapter: TableListAdapter
    private lateinit var pgsBar: ProgressBar
    private lateinit var tournFilterData: ArrayList<FilterListItemModel>

    private lateinit var tvStat1: TextView
    private lateinit var tvStat2: TextView
    private lateinit var tvStat3: TextView

    private var sortBy : Int = SORT_BY_STAT_3_DESC

    companion object {

        const val STAT_TYPE_HITS = 0
        const val STAT_TYPE_SLUGS = 1
        const val STAT_TYPE_MATCHES = 2
        const val STAT_TYPE_TOURN = 3
        const val STAT_TYPE_ERROR = -1

        const val ENTRY_PLAYERS = 10
        const val ENTRY_TEAMS = 20

        const val SORT_BY_STAT_1_DESC = 109
        const val SORT_BY_STAT_1_ASC = 119
        const val SORT_BY_STAT_2_DESC = 209
        const val SORT_BY_STAT_2_ASC = 219
        const val SORT_BY_STAT_3_DESC = 309
        const val SORT_BY_STAT_3_ASC = 319

        @JvmStatic
        fun newInstance(statType: Int) =
            RankingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_STAT_TYPE, statType)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            statType = it.getInt(ARG_STAT_TYPE)
        }

        dbHelper = DataBaseHelper(context)

        tournFilterData = loadTournFilterData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = if(statType == STAT_TYPE_SLUGS) {
            inflater.inflate(R.layout.fragment_ranking_1, container, false)
        } else {
            inflater.inflate(R.layout.fragment_ranking_3, container, false)
        }

        setHasOptionsMenu(true)

        pgsBar = addProgressBar(root as ConstraintLayout, activity as Context)
        pgsBar.visibility = View.VISIBLE

        setUpTable(root)

        return root
    }

    private fun setUpTable(root: View) {

        tvStat1 = root.findViewById(R.id.tv_table_stat1)
        if(statType != STAT_TYPE_SLUGS) {
            tvStat2 = root.findViewById(R.id.tv_table_stat2)
            tvStat3 = root.findViewById(R.id.tv_table_stat3)
        }

        setUpHeaderTitles()

        setUpSortClickListener()

        if(statType == STAT_TYPE_SLUGS)
            sortBy = SORT_BY_STAT_1_ASC

        updateSortArrows(sortBy)

        //start loading data in background thread
        val loadParams = LoadParams(statType!!, entryType)
        val asyncTask = DatabaseAsyncTask(this)
        asyncTask.execute(loadParams)

        val tableViewManager = LinearLayoutManager(activity)
        val numbStats: Int = if (statType == STAT_TYPE_SLUGS) 1 else 3

        tableAdapter = TableListAdapter(dataList, activity as Context, numbStats)

        root.findViewById<RecyclerView>(R.id.rv_frag_table).apply {
            setHasFixedSize(true)
            layoutManager = tableViewManager
            adapter = tableAdapter
        }
    }

    private fun updateTableSorted(dataList_: ArrayList<TableEntryModel>?) {
        if (dataList_ == null) return
        dataList.clear()
        dataList.addAll(dataList_)
        sortDataList()
    }

    private fun updateTableFilter(filterTournIDs: List<String>) {
        pgsBar.visibility = View.VISIBLE

        val loadParams = LoadParams(statType!!, entryType, filterTournIDs)
        val asyncTask = DatabaseAsyncTask(this)
        asyncTask.execute(loadParams)
    }

    private fun updateSortArrows(newSortBy: Int) {
        val colorOff = ContextCompat.getColor(this.requireContext(), R.color.text_mid)
        val colorOn= ContextCompat.getColor(this.requireContext(), R.color.colorPrimaryLight)

        when(sortBy) {
            SORT_BY_STAT_1_DESC -> (tvStat1.compoundDrawables[0] as LayerDrawable)
                .findDrawableByLayerId(R.id.ic_sort_arrow_down).setTint(colorOff)
            SORT_BY_STAT_1_ASC -> (tvStat1.compoundDrawables[0] as LayerDrawable)
                .findDrawableByLayerId(R.id.ic_sort_arrow_up).setTint(colorOff)
            SORT_BY_STAT_2_DESC -> (tvStat2.compoundDrawables[0] as LayerDrawable)
                .findDrawableByLayerId(R.id.ic_sort_arrow_down).setTint(colorOff)
            SORT_BY_STAT_2_ASC -> (tvStat2.compoundDrawables[0] as LayerDrawable)
                .findDrawableByLayerId(R.id.ic_sort_arrow_up).setTint(colorOff)
            SORT_BY_STAT_3_DESC -> (tvStat3.compoundDrawables[0] as LayerDrawable)
                .findDrawableByLayerId(R.id.ic_sort_arrow_down).setTint(colorOff)
            SORT_BY_STAT_3_ASC -> (tvStat3.compoundDrawables[0] as LayerDrawable)
                .findDrawableByLayerId(R.id.ic_sort_arrow_up).setTint(colorOff)
        }

        when(newSortBy) {
            SORT_BY_STAT_1_DESC -> (tvStat1.compoundDrawables[0] as LayerDrawable)
                .findDrawableByLayerId(R.id.ic_sort_arrow_down).setTint(colorOn)
            SORT_BY_STAT_1_ASC -> (tvStat1.compoundDrawables[0] as LayerDrawable)
                .findDrawableByLayerId(R.id.ic_sort_arrow_up).setTint(colorOn)
            SORT_BY_STAT_2_DESC -> (tvStat2.compoundDrawables[0] as LayerDrawable)
                .findDrawableByLayerId(R.id.ic_sort_arrow_down).setTint(colorOn)
            SORT_BY_STAT_2_ASC -> (tvStat2.compoundDrawables[0] as LayerDrawable)
                .findDrawableByLayerId(R.id.ic_sort_arrow_up).setTint(colorOn)
            SORT_BY_STAT_3_DESC -> (tvStat3.compoundDrawables[0] as LayerDrawable)
                .findDrawableByLayerId(R.id.ic_sort_arrow_down).setTint(colorOn)
            SORT_BY_STAT_3_ASC -> (tvStat3.compoundDrawables[0] as LayerDrawable)
                .findDrawableByLayerId(R.id.ic_sort_arrow_up).setTint(colorOn)
        }
    }

    private fun sortDataList() {
        when(sortBy) {
            SORT_BY_STAT_1_ASC -> dataList.sortWith { t, t2 -> t.compareTo(t2, 1) }
            SORT_BY_STAT_1_DESC -> dataList.sortWith { t, t2 -> t2.compareTo(t, 1) }
            SORT_BY_STAT_2_ASC -> dataList.sortWith { t, t2 -> t.compareTo(t2, 2) }
            SORT_BY_STAT_2_DESC -> dataList.sortWith { t, t2 -> t2.compareTo(t, 2) }
            SORT_BY_STAT_3_ASC -> dataList.sortWith { t, t2 -> t.compareTo(t2, 3) }
            SORT_BY_STAT_3_DESC -> dataList.sortWith { t, t2 -> t2.compareTo(t, 3) }
        }
        tableAdapter.notifyDataSetChanged()
    }

    private fun setUpSortClickListener() {
        tvStat1.setOnClickListener {
            tableHeaderClicked(SORT_BY_STAT_1_ASC, SORT_BY_STAT_1_DESC)
        }

        if(statType == STAT_TYPE_SLUGS) return

        tvStat2.setOnClickListener {
            tableHeaderClicked(SORT_BY_STAT_2_ASC, SORT_BY_STAT_2_DESC)
        }
        tvStat3.setOnClickListener {
            tableHeaderClicked(SORT_BY_STAT_3_ASC, SORT_BY_STAT_3_DESC)
        }
    }

    private fun tableHeaderClicked(asc: Int, desc: Int) {
        sortBy = when(sortBy) {
            desc -> {
                updateSortArrows(asc)
                asc
            }
            else -> {
                updateSortArrows(desc)
                desc
            }
        }
        sortDataList()
    }

    private fun setUpHeaderTitles() {
        when (statType) {
            STAT_TYPE_HITS -> {
                tvStat1.text = getString(R.string.header_shots)
                tvStat2.text = getString(R.string.header_hits)
            }
            STAT_TYPE_MATCHES -> {
                tvStat1.text = getString(R.string.header_matches)
                tvStat2.text = getString(R.string.header_wins)
            }
            STAT_TYPE_TOURN -> {
                tvStat1.text = getString(R.string.heade_tourn)
                tvStat2.text = getString(R.string.header_wins)
            }
            STAT_TYPE_SLUGS -> tvStat1.text = getString(R.string.header_slugs)
        }




    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("Sven", "called")
        when (item.itemId) {
            R.id.menu_rankings_filter -> {
                openFilterAlertDialog(item)
            }
        }

        return true
    }

    private fun openFilterAlertDialog(item: MenuItem) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity as Context , R.style.DialogStyle)
        builder.setTitle(item.title)

        val view: View = ConstraintLayout.inflate(activity as Context, R.layout.inflatable_dialog_filter_r, null)

        buildFilterRecView(tournFilterData, view)

        builder.setView(view)

        val dialog = builder.create()

        val ctvPlayers = view.findViewById<CheckedTextView>(R.id.ctv_filter_r_players)
        val ctvTeams = view.findViewById<CheckedTextView>(R.id.ctv_filter_r_teams)

        ctvPlayers.isChecked = (entryType == ENTRY_PLAYERS)
        ctvTeams.isChecked = (entryType == ENTRY_TEAMS)

        ctvPlayers.setOnClickListener {
            if(entryType == ENTRY_TEAMS) {
                entryType = ENTRY_PLAYERS
                ctvPlayers.isChecked = true
                ctvTeams.isChecked = false
            }
        }

        ctvTeams.setOnClickListener {
            if(entryType == ENTRY_PLAYERS) {
                entryType = ENTRY_TEAMS
                ctvPlayers.isChecked = false
                ctvTeams.isChecked = true
            }
        }

        view.findViewById<Button>(R.id.btn_filter_dialog_cacel).setOnClickListener {
            Handler().postDelayed( {
                dialog.cancel()
            }, 150)
        }

        view.findViewById<Button>(R.id.btn_filter_dialog_ok).setOnClickListener {

            val filterTournIDs: ArrayList<String> = arrayListOf()
            tournFilterData.forEach{
                if(it.checked) {
                    filterTournIDs.add(it.id)
                }
            }

            updateTableFilter(filterTournIDs)

            Log.d("Sven", "OK clicked")
            Handler().postDelayed( {
                dialog.cancel()
            }, 150)
        }

        dialog.show()
    }

    private fun buildFilterRecView(dataset: ArrayList<FilterListItemModel>, view: View) {
        val viewManager = LinearLayoutManager(activity as Context)
        val viewAdapter = FilterListAdapter(dataset, activity as Context)

        view.findViewById<RecyclerView>(R.id.rv_Tourns).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            addItemDecoration(SimpleDividerItemDecoration(context, DPconvertion.toDP(40F, context), DPconvertion.toDP(40F, context)))
        }
    }

    private fun loadTournFilterData(): ArrayList<FilterListItemModel> {
        val tournMap = dbHelper.getIDandName(DataBaseHelper.TABLE_TOURNAMENTS)
        val resList: ArrayList<FilterListItemModel> = arrayListOf()

        tournMap.forEach { (id, name) ->
            val item = FilterListItemModel(id = id, name = name)
            resList.add(item)
        }

        return resList
    }


    private class DatabaseAsyncTask(activity: RankingFragment): AsyncTask<LoadParams, Int, ArrayList<TableEntryModel>>() {

        private var activityWeakReference: WeakReference<RankingFragment>? = null

        init {
            activityWeakReference = WeakReference<RankingFragment>(activity)
        }


        override fun doInBackground(vararg loadParams: LoadParams?): ArrayList<TableEntryModel> {

            if (loadParams.isEmpty()) return arrayListOf()

            val activity: RankingFragment? = activityWeakReference?.get()
            if (activity == null || activity.requireActivity().isFinishing) {
                return arrayListOf()
            }

            val dbHelper = DataBaseHelper(activity.context)

            val entryMap = if(loadParams[0]!!.entryType == ENTRY_PLAYERS) {
                dbHelper.getIDandName(DataBaseHelper.TABLE_PLAYERS)
            } else {
                dbHelper.getIDandName(DataBaseHelper.TABLE_TEAMS)
            }

            return when(loadParams[0]!!.statType) {
                STAT_TYPE_HITS -> loadHitsData(loadParams[0]!!, dbHelper, entryMap)
                STAT_TYPE_SLUGS -> loadAvgSlugs(loadParams[0]!!, dbHelper, entryMap)
                STAT_TYPE_MATCHES -> loadMatchesData(loadParams[0]!!, dbHelper, entryMap)
                STAT_TYPE_TOURN -> loadTournData(loadParams[0]!!, dbHelper, entryMap)
                else -> arrayListOf()
            }

        }

        override fun onPostExecute(result: ArrayList<TableEntryModel>?) {
            super.onPostExecute(result)

            val activity: RankingFragment? = activityWeakReference?.get()
            if (activity == null || activity.requireActivity().isFinishing) {
                return
            }
            activity.updateTableSorted(result)
            activity.pgsBar.visibility = View.INVISIBLE

        }

        private fun loadHitsData(loadParams: LoadParams, dbHelper: DataBaseHelper, entryList: List<EntryModel>): ArrayList<TableEntryModel> {

            val dataSet = arrayListOf<TableEntryModel>()

            entryList.forEach{
                val shotsStats: List<Int> = if(loadParams.entryType == ENTRY_PLAYERS && loadParams.filterTournIDs == null) {
                    dbHelper.getPlayerSumShotsStats(it.entryID)
                } else if (loadParams.entryType == ENTRY_PLAYERS && loadParams.filterTournIDs != null) {
                    dbHelper.getPlayerSumShotsStats(it.entryID, loadParams.filterTournIDs)
                } else if (loadParams.entryType == ENTRY_TEAMS && loadParams.filterTournIDs == null) {
                    dbHelper.getTeamSumShotsStats(it.entryID)
                } else if (loadParams.entryType == ENTRY_TEAMS && loadParams.filterTournIDs != null) {
                    dbHelper.getTeamSumShotsStats(it.entryID, loadParams.filterTournIDs)
                } else {
                    throw IllegalArgumentException()
                }

                if(shotsStats[0] != 0) {
                    val ratio: Float =( shotsStats[1].toFloat() / shotsStats[0].toFloat() ) * 100F
                    val ratioS = String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_1, ratio) + "%"
                    dataSet.add(
                        TableEntryModel(
                            name = it.entryName,
                            stat1 = shotsStats[0].toString(),
                            stat2 = shotsStats[1].toString(),
                            stat3 = ratioS
                        )
                    )
                }

            }

            return dataSet
        }

        private fun loadAvgSlugs(loadParams: LoadParams, dbHelper: DataBaseHelper, entryList: List<EntryModel>): ArrayList<TableEntryModel> {

            val dataSet = arrayListOf<TableEntryModel>()

            entryList.forEach{
                val avgSlugs: Float= if(loadParams.entryType == ENTRY_PLAYERS && loadParams.filterTournIDs == null) {
                    dbHelper.getPlayerAvgSlugs(it.entryID)
                } else if (loadParams.entryType == ENTRY_PLAYERS && loadParams.filterTournIDs != null) {
                    dbHelper.getPlayerAvgSlugs(it.entryID, null, loadParams.filterTournIDs)
                } else if (loadParams.entryType == ENTRY_TEAMS && loadParams.filterTournIDs == null) {
                    dbHelper.getTeamAvgSlugs(it.entryID)
                } else if (loadParams.entryType == ENTRY_TEAMS && loadParams.filterTournIDs != null) {
                    dbHelper.getTeamAvgSlugs(it.entryID, loadParams.filterTournIDs)
                } else {
                    throw IllegalArgumentException()
                }

                if (avgSlugs != 0F) {
                    val avgSlugsS = String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_1, avgSlugs)
                    dataSet.add(
                        TableEntryModel(
                            name = it.entryName,
                            stat1 = avgSlugsS,
                            stat2 = null,
                            stat3 = null
                        )
                    )
                }

            }

            return dataSet
        }

        private fun loadMatchesData(loadParams: LoadParams, dbHelper: DataBaseHelper, entryList: List<EntryModel>): ArrayList<TableEntryModel> {

            val dataSet = arrayListOf<TableEntryModel>()

            entryList.forEach{
                val matchStats: List<Int> = if(loadParams.entryType == ENTRY_PLAYERS && loadParams.filterTournIDs == null) {
                    dbHelper.getPlayerMatchNumbers(it.entryID)
                } else if (loadParams.entryType == ENTRY_PLAYERS && loadParams.filterTournIDs != null) {
                    dbHelper.getPlayerMatchNumbers(it.entryID, null, loadParams.filterTournIDs)
                } else if (loadParams.entryType == ENTRY_TEAMS && loadParams.filterTournIDs == null) {
                    dbHelper.getTeamMatchStats(it.entryID)
                } else if (loadParams.entryType == ENTRY_TEAMS && loadParams.filterTournIDs != null) {
                    dbHelper.getTeamMatchStats(it.entryID, loadParams.filterTournIDs)
                } else {
                    throw IllegalArgumentException()
                }

                if(matchStats[0] != 0) {
                    val ratio: Float = (matchStats[1].toFloat() / matchStats[0].toFloat()) * 100F
                    val ratioS =
                        String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_1, ratio) + "%"
                    dataSet.add(
                        TableEntryModel(
                            name = it.entryName,
                            stat1 = matchStats[0].toString(),
                            stat2 = matchStats[1].toString(),
                            stat3 = ratioS
                        )
                    )
                }

            }

            return dataSet
        }

        private fun loadTournData(loadParams: LoadParams, dbHelper: DataBaseHelper, entryList: List<EntryModel>): ArrayList<TableEntryModel> {

            val dataSet = arrayListOf<TableEntryModel>()

            entryList.forEach{
                val shotsStats: List<Int> = if(loadParams.entryType == ENTRY_PLAYERS && loadParams.filterTournIDs == null) {
                    dbHelper.getPlayerTournStats(it.entryID)
                } else if (loadParams.entryType == ENTRY_PLAYERS && loadParams.filterTournIDs != null) {
                    dbHelper.getPlayerTournStats(it.entryID, null, loadParams.filterTournIDs)
                } else if (loadParams.entryType == ENTRY_TEAMS && loadParams.filterTournIDs == null) {
                    dbHelper.getTeamTournStats(it.entryID)
                } else if (loadParams.entryType == ENTRY_TEAMS && loadParams.filterTournIDs != null) {
                    dbHelper.getTeamTournStats(it.entryID, loadParams.filterTournIDs)
                } else {
                    throw IllegalArgumentException()
                }

                if(shotsStats[0] != 0) {
                    val ratio: Float = (shotsStats[1].toFloat() / shotsStats[0].toFloat()) * 100F
                    val ratioS =
                        String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_1, ratio) + "%"
                    dataSet.add(
                        TableEntryModel(
                            name = it.entryName,
                            stat1 = shotsStats[0].toString(),
                            stat2 = shotsStats[1].toString(),
                            stat3 = ratioS
                        )
                    )
                }
            }

            return dataSet
        }

    }


    private data class LoadParams (
        val statType: Int,
        val entryType: Int,
        val filterTournIDs: List<String>? = null
    )
}