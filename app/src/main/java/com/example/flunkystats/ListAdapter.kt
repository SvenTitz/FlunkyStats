package com.example.flunkystats

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flunkystats.models.ListEntryModel

class ListAdapter(
    private val dataset: ArrayList<ListEntryModel>,
    private val infoStartString: String,
    private val context: Context,
    private val intentClass: Class<*>
) : RecyclerView.Adapter<ListAdapter.ListViewHolder>(), Filterable {

    private val datasetFull = dataset.toList()

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_card_name)
        val tvInfo: TextView = itemView.findViewById(R.id.tv_card_info)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.inflatable_list_card_view, parent, false) as View

        view.setOnClickListener {
            val entryID = view.findViewById<TextView>(R.id.tv_card_name).tag as String
            val intent = Intent(context, intentClass).apply {
                putExtra(AppConfig.EXTRA_MESSAGE_ENTRY_ID, entryID)
            }
            context.startActivity(intent)
        }

        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.tvName.text = dataset[position].entryName
        holder.tvName.tag = dataset[position].entryID
        val infoText = infoStartString + dataset[position].getInfoString()
        holder.tvInfo.text = infoText
    }

    override fun getItemCount() = dataset.size


    override fun getFilter(): Filter {
        return nameFilter
    }

    private val nameFilter = object : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val filteredData: ArrayList<ListEntryModel> = arrayListOf()

            if (p0 == null || p0.isEmpty()) {
                filteredData.addAll(datasetFull)
            } else {
                val filterPatter = p0.toString().trim()

                for (item in datasetFull) {
                    if(item.entryName.contains(filterPatter, true) || item.getInfoString().contains(filterPatter, true)) {
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
            dataset.addAll(p1?.values as Collection<ListEntryModel>)
            notifyDataSetChanged()
        }
    }

}


