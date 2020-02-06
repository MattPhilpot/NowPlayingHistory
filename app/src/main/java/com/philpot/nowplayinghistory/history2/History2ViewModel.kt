package com.philpot.nowplayinghistory.history2

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.repo.HistoryListRepository
import com.philpot.nowplayinghistory.repo.SyncResult
import com.philpot.nowplayinghistory.viewmodel.RefreshableViewModel

class History2ViewModel(private val historyListRepository: HistoryListRepository) : RefreshableViewModel<PagedList<HistoryItem>>() {

    override fun getData(): LiveData<SyncResult<PagedList<HistoryItem>>> = historyListRepository.getHistory()
}
