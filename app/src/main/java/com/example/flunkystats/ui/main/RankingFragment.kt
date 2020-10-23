package com.example.flunkystats.ui.main

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.flunkystats.adapter.TableListAdapter
import com.example.flunkystats.database.DataBaseHelper
import com.example.flunkystats.models.TableEntryModel
import org.w3c.dom.Text
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList


// the fragment initialization parameters
private const val ARG_STAT_TYPE = "statType"

class RankingFragment : Fragment(), LoadsData {

    private var statType: Int? = null
    private var dataList: ArrayList<TableEntryModel> = arrayListOf()

    private lateinit var dbHelper: DataBaseHelper
    private lateinit var tableAdapter: TableListAdapter
    private lateinit var pgsBar: ProgressBar

    private lateinit var tvStat1: TextView
    private lateinit var tvStat2: TextView
    private lateinit var tvStat3: TextView

    private var sortBy : Int = SORT_BY_STAT_3_DESC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            statType = it.getInt(ARG_STAT_TYPE)
        }

        dbHelper = DataBaseHelper(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_ranking, container, false)

        pgsBar = addProgressBar(root as ConstraintLayout, activity as Context)
        pgsBar.visibility = View.VISIBLE

        setUpTable(root)

        return root
    }


    private fun setUpTable(root: View) {

        tvStat1 =root.findViewById<TextView>(R.id.tv_table_stat1)
        tvStat2 = root.findViewById<TextView>(R.id.tv_table_stat2)
        tvStat3 = root.findViewById<TextView>(R.id.tv_table_stat3)

        tvStat1.text = "Würfe"
        tvStat2.text = "Treffer"
        tvStat3.text = "Quote"

        tvStat1.setOnClickListener {
            sortBy = when(sortBy) {
                SORT_BY_STAT_1_DESC -> {
                    updateSortArrows(SORT_BY_STAT_1_ASC)
                    SORT_BY_STAT_1_ASC
                }
                else -> {
                    updateSortArrows(SORT_BY_STAT_1_DESC)
                    SORT_BY_STAT_1_DESC
                }
            }
            sortDataList()
        }
        tvStat2.setOnClickListener {
            sortBy = when(sortBy) {
                SORT_BY_STAT_2_DESC -> {
                    updateSortArrows(SORT_BY_STAT_2_ASC)
                    SORT_BY_STAT_2_ASC
                }
                else -> {
                    updateSortArrows(SORT_BY_STAT_2_DESC)
                    SORT_BY_STAT_2_DESC
                }
            }
            sortDataList()
        }
        tvStat3.setOnClickListener {
            sortBy = when(sortBy) {
                SORT_BY_STAT_3_DESC -> {
                    updateSortArrows(SORT_BY_STAT_3_ASC)
                    SORT_BY_STAT_3_ASC
                }
                else -> {
                    updateSortArrows(SORT_BY_STAT_3_DESC)
                    SORT_BY_STAT_3_DESC
                }
            }
            sortDataList()
        }

        updateSortArrows(sortBy)

        //start loading data in background thread
        val asyncTask = DatabaseAsyncTask(this)
        asyncTask.execute()

        val tableViewManager = LinearLayoutManager(activity)
        tableAdapter = TableListAdapter(dataList, activity as Context)

        root.findViewById<RecyclerView>(R.id.rv_frag_table).apply {
            setHasFixedSize(true)
            layoutManager = tableViewManager
            adapter = tableAdapter
        }
    }

    private fun updateTable(dataList_: ArrayList<TableEntryModel>?) {
        if (dataList_ == null) return
        dataList.clear()
        dataList.addAll(dataList_)
        sortDataList()
    }

    private fun updateSortArrows(newSortBy: Int) {
        val colorOff = ContextCompat.getColor(this.context!!, R.color.text_mid)
        val colorOn= ContextCompat.getColor(this.context!!, R.color.colorPrimaryLight)

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

    companion object {

        const val STAT_TYPE_HITS = 0
        const val STAT_TYPE_SLUGS = 1
        const val STAT_TYPE_MATCHES = 2
        const val STAT_TYPE_TOURN = 3
        const val STAT_TYPE_ERROR = -1

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

    private class DatabaseAsyncTask(activity: RankingFragment): AsyncTask<Int, Int, ArrayList<TableEntryModel>>() {

        private var activityWeakReference: WeakReference<RankingFragment>? = null

        init {
            activityWeakReference = WeakReference<RankingFragment>(activity)
        }


        override fun doInBackground(vararg p0: Int?): ArrayList<TableEntryModel> {

            val activity: RankingFragment? = activityWeakReference?.get()
            if (activity == null || activity.activity!!.isFinishing) {
                return arrayListOf()
            }

            val dbHelper = DataBaseHelper(activity.context)

            val playerMap = dbHelper.getIDandName(DataBaseHelper.TABLE_PLAYERS)

            val playerSet = arrayListOf<TableEntryModel>()

            playerMap.forEach{ (id, name) ->
                val shots = dbHelper.getPlayerSumShots(id)
                val hits = dbHelper.getPlayerSumHits(id)
                val ratio = ( hits.toFloat() / shots.toFloat() ) * 100F
                val ratioS = String.format(Locale.ENGLISH, AppConfig.FLOAT_FORMAT_1, ratio) + "%"
                playerSet.add(
                    TableEntryModel(
                        name = name,
                        stat1 = shots.toString(),
                        stat2 = hits.toString(),
                        stat3 = ratioS
                    )
                )
            }

            return playerSet
        }

        override fun onPostExecute(result: ArrayList<TableEntryModel>?) {
            super.onPostExecute(result)

            val activity: RankingFragment? = activityWeakReference?.get()
            if (activity == null || activity.activity!!.isFinishing) {
                return
            }
            activity.updateTable(result)
            activity.pgsBar.visibility = View.INVISIBLE

        }
    }
}