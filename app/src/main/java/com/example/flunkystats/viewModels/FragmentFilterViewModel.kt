package com.example.flunkystats.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flunkystats.database.DataBaseHelper
import com.example.flunkystats.models.FilterListItemModel
import com.example.flunkystats.ui.main.RankingFragment

class FragmentFilterViewModel(application: Application) : AndroidViewModel(application){

    private var dbHelper: DataBaseHelper = DataBaseHelper(application)

    var tournFilterData = MutableLiveData<ArrayList<FilterListItemModel>>(loadTournFilterData())

    var entryType = MutableLiveData(RankingFragment.ENTRY_PLAYERS)

    private fun loadTournFilterData(): ArrayList<FilterListItemModel> {
        val tournMap = dbHelper.getIDandName(DataBaseHelper.TABLE_TOURNAMENTS)
        val resList: ArrayList<FilterListItemModel> = arrayListOf()

        tournMap.forEach { (id, name) ->
            val item = FilterListItemModel(id = id, name = name)
            resList.add(item)
        }

        return resList
    }


}