package com.example.flunkystats.adapter

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.flunkystats.R
import com.example.flunkystats.models.TableEntryModel

class TableListAdapter (
    private var dataset: ArrayList<TableEntryModel>,
    private val context: Context
) : RecyclerView.Adapter<TableListAdapter.ListViewHolder>() {

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvNumb: TextView = itemView.findViewById(R.id.tv_table_numb)
        val tvName: TextView = itemView.findViewById(R.id.tv_table_name)
        val tvStat1: TextView = itemView.findViewById(R.id.tv_table_stat1)
        val tvStat2: TextView = itemView.findViewById(R.id.tv_table_stat2)
        val tvStat3: TextView = itemView.findViewById(R.id.tv_table_stat3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View

        when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.inflatable_table_row_3, parent, false) as View
                view.findViewById<LinearLayout>(R.id.ll_table_row_root).setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundMidLight))
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.inflatable_table_row_3, parent, false) as View
                view.elevation = 3*context.resources.displayMetrics.density
            }
        }
        return ListViewHolder(view)
    }

    override fun getItemCount() = dataset.size

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return 0
        } else {
            return (position % 2)
        }
    }


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
            holder.tvNumb.text = (position+1).toString()
            holder.tvName.text = dataset[position].name
            holder.tvStat1.text = dataset[position].stat1
            holder.tvStat2.text = dataset[position].stat2
            holder.tvStat3.text = dataset[position].stat3
    }

    fun updateDataset(dataset_: ArrayList<TableEntryModel>) {
        this.dataset = dataset_
        notifyDataSetChanged()
    }


}