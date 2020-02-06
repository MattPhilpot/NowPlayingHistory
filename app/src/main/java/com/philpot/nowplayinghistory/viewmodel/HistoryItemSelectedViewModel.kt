package com.philpot.nowplayinghistory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.philpot.nowplayinghistory.model.SongInfo

class HistoryItemSelectedViewModel {

    private val selected = MutableLiveData<SongInfo>()

    val selectedSong: LiveData<SongInfo>
        get() = selected

    fun onItemClick(item: SongInfo) {
        selected.value = item
    }
}