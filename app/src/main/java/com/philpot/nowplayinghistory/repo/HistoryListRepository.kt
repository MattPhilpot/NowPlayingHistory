package com.philpot.nowplayinghistory.repo

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.philpot.nowplayinghistory.coroutine.CoroutineContextProvider
import com.philpot.nowplayinghistory.db2.dao.HistoryDao
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.HistoryItem

class HistoryListRepository(private val historyDao: HistoryDao,
                            coroutineContextProvider: CoroutineContextProvider) : BaseRepository(coroutineContextProvider) {

    fun getHistory() = databaseOnlyLiveData(
        databaseQuery = { LivePagedListBuilder(historyDao.getHistory(), 50).build() }
    )
}
