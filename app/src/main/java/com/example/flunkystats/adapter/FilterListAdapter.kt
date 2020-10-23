package com.example.flunkystats.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.flunkystats.R
import com.example.flunkystats.models.FilterListItemModel

class FilterListAdapter(
    private val dataset: ArrayList<FilterListItemModel>,
    private val context: Context
) : RecyclerView.Adapter<FilterListAdapter.ListViewHolder>() {

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cvtItem: CheckedTextView = itemView.findViewById(R.id.ctv_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.inflatable_filter_card, parent, false) as View

        return ListViewHolder(view)
    }

    override fun getItemCount() = dataset.size


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.cvtItem.text = dataset[position].name
        holder.cvtItem.tag = dataset[position].id
        holder.cvtItem.isChecked = dataset[position].checked

        holder.cvtItem.setOnClickListener{
            if (atLeastTwoChecked() || !(it as CheckedTextView).isChecked) {
                (it as CheckedTextView).isChecked = !it.isChecked
                dataset[position].checked = it.isChecked
            } else {
                val toast = Toast.makeText(context, "Mindestens eine Auswahl muss aktiv sein", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    private fun atLeastTwoChecked(): Boolean {
        var count = 0
        dataset.forEach {
            if(it.checked) {
                count++
                if (count == 2) return true
            }
        }
        return false
    }

}


