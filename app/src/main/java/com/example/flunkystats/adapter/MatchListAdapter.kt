package com.example.flunkystats.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.flunkystats.AppConfig
import com.example.flunkystats.R
import com.example.flunkystats.activities.MatchStatsActivity
import com.example.flunkystats.models.ListEntryModel
import com.example.flunkystats.models.ListMatchModel
import java.util.*
import kotlin.collections.ArrayList

class MatchListAdapter(
    private val dataset: ArrayList<ListMatchModel>,
    private val context: Context
) : RecyclerView.Adapter<MatchListAdapter.ListViewHolder>(), Filterable {

    private val datasetFull: ArrayList<ListMatchModel> = ArrayList(dataset)
    private val clTeamsList = arrayListOf<ConstraintLayout>()
    private var flag_team_filtered = false

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvName1: TextView = itemView.findViewById(R.id.tv_card_matches_team1)
        val tvName2: TextView = itemView.findViewById(R.id.tv_card_matches_team2)
        val tvInfo1: TextView = itemView.findViewById(R.id.tv_card_matches_info1)
        val tvInfo2: TextView = itemView.findViewById(R.id.tv_card_matches_info2)
        val tvVS: TextView = itemView.findViewById(R.id.tv_card_matches_vs)
        val clTeams: ConstraintLayout = itemView.findViewById(R.id.cl_m_stats_teams)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.inflatable_match_card, parent, false) as View

        val clTeams = view.findViewById<ConstraintLayout>(R.id.cl_m_stats_teams)

        clTeams.setOnClickListener {
            val matchID = view.findViewById<TextView>(R.id.tv_card_matches_vs).tag as String
            val intent = Intent(context, MatchStatsActivity::class.java).apply {
                putExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID, matchID)
            }
            context.startActivity(intent)
        }


        clTeamsList.add(clTeams)

        view.findViewById<ConstraintLayout>(R.id.cl_m_stats_info).setOnClickListener {
            if (clTeams.visibility == View.GONE) {
                clTeams.visibility = View.VISIBLE
            } else {
                clTeams.visibility = View.GONE
            }

        }

        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.tvName1.text = dataset[position].team1Name
        holder.tvName1.tag = dataset[position].team1ID
        holder.tvName2.text = dataset[position].team2Name
        holder.tvName2.tag = dataset[position].team2ID
        holder.tvVS.tag = dataset[position].matchID
        holder.tvInfo1.text = dataset[position].matchInfo[0]
        val info2Text = context.getString(R.string.spiel) + dataset[position].matchNumb
        holder.tvInfo2.text = info2Text

        if (dataset.size != datasetFull.size && flag_team_filtered) {
            clTeamsList.forEach {
                it.visibility = View.VISIBLE
            }
        } else {
            clTeamsList.forEach {
                it.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = dataset.size


    override fun getFilter(): Filter {
        return nameFilter
    }

    private val nameFilter = object : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val filteredData: ArrayList<ListMatchModel> = arrayListOf()

            if (p0 == null || p0.isEmpty()) {
                filteredData.addAll(datasetFull)
                flag_team_filtered = false
            } else {
                val filterPatter = p0.toString().trim()

                for (item in datasetFull) {
                    if (item.team1Name.contains(filterPatter, true) ||
                        item.team2Name.contains(filterPatter, true))
                    {
                        filteredData.add(item)
                        flag_team_filtered = true
                    } else if(item.matchInfo.toString().contains(filterPatter, true) ||
                        item.matchNumb.toString().contains(filterPatter, true))
                    {
                        filteredData.add(item)
                    }
                }
            }


            val res = FilterResults()
            res.values = filteredData
            return res
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            dataset.clear()
            dataset.addAll(p1?.values as Collection<ListMatchModel>)

            notifyDataSetChanged()
        }
    }

    fun addEntry(entry: ListMatchModel) {
        dataset.add(entry)
        dataset.sortWith(compareBy({ it.matchInfo[0].toLowerCase(Locale.ROOT) }, { it.matchNumb }))
        datasetFull.add(entry)
        datasetFull.sortWith(compareBy({ it.matchInfo[0].toLowerCase(Locale.ROOT) }, { it.matchNumb }))
        notifyDataSetChanged()
    }

    fun updateDataset(newDataset: ArrayList<ListMatchModel>) {
        dataset.clear()
        dataset.addAll(newDataset)
        datasetFull.clear()
        datasetFull.addAll(dataset)
        notifyDataSetChanged()
    }

}


