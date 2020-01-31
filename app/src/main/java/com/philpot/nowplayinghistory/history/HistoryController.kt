package com.philpot.nowplayinghistory.history

import com.philpot.nowplayinghistory.db.dao.HistoryDao
import com.philpot.nowplayinghistory.model.HistoryItem
import android.content.SharedPreferences
import com.philpot.nowplayinghistory.db.manager.SongAlbumManager
import com.philpot.nowplayinghistory.event.EventBus
import com.philpot.nowplayinghistory.event.NewHistoryItemEvent
import com.philpot.nowplayinghistory.event.SettingsChangedEvent
import com.philpot.nowplayinghistory.model.Preferences
import com.philpot.nowplayinghistory.util.TestRecordInsertUtil
import com.philpot.nowplayinghistory.widget.RecyclerViewPaginationListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by MattPhilpot on 12/2/2017.
 */
class HistoryController(private val historyDao: HistoryDao,
                        private val songAlbumManager: SongAlbumManager,
                        private val eventBus: EventBus,
                        private val preferences: SharedPreferences,
                        private var view: HistoryView?) : RecyclerViewPaginationListener.RecyclerViewPaginationCallback {

    companion object {
        private val PAGE_SIZE = 250

        private var isCurrentlyLoading = false
        private var currentEndStamp: Long? = null
        private var endOfPages = false
    }

    fun initialize() {
        //TestRecordInsertUtil.insertTestRecords(historyDao)
        //TestRecordInsertUtil.insertNewRandomRecord(historyDao, songInfoDao)
        loadMoreItems()
        eventBus.register(this)
    }

    fun onResume(firstItemId: Long?) {
        firstItemId?.let {
            view?.addHistoryItemsToTop(historyDao.getHistoryAfter(it))
            return
        }

        forceReloadList()
    }

    private fun forceReloadList() {
        Runnable {
            while (isCurrentlyLoading) {
                Thread.sleep(100)
            }
            isCurrentlyLoading = true
            currentEndStamp = null
            view?.replaceHistoryListWith(getPaginatedListInternal())
            isCurrentlyLoading = false
        }.run()
    }

    fun destroy() {
        view = null
        eventBus.unregister(this)
    }

    fun doDeleteHistoryItems(list: List<HistoryItem>) {
        list.forEach {
            historyDao.delete(it)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: NewHistoryItemEvent) {
        view?.addHistoryItemToTop(event.historyItem)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onEvent(event: SettingsChangedEvent) {
        if (event.setting == Preferences.LastFmIntegration || event.setting == Preferences.CurrentMusicApp) {
            view?.updateListAdapterSettings()
        }
    }

    override fun isLoading(): Boolean = isCurrentlyLoading

    override fun isLastPage(): Boolean = endOfPages

    override fun loadMoreItems() {
        Runnable {
            if (!isCurrentlyLoading) {
                isCurrentlyLoading = true
                view?.addPaginatedHistory(getPaginatedListInternal())
                isCurrentlyLoading = false
            }
        }.run()
    }

    private fun getPaginatedListInternal(): List<HistoryItem> {
        val itemList = historyDao.getPaginatedHistoryList(PAGE_SIZE, currentEndStamp)
        endOfPages = itemList.size < PAGE_SIZE
        if (!endOfPages) {
            currentEndStamp = itemList.last().timestamp
        }

        songAlbumManager.allowLastFM = isLastFMEnabled()
        for(each in itemList) {
            each.songInfo = songAlbumManager.getSongInfoFor(each)
        }

        return itemList
    }

    private fun isLastFMEnabled(): Boolean {
        return preferences.getBoolean(Preferences.LastFmIntegration.value, false)
    }

    interface HistoryView {
        fun replaceHistoryListWith(list: List<HistoryItem>)
        fun addHistoryItemToTop(item: HistoryItem)
        fun addHistoryItemsToTop(list: List<HistoryItem>)
        fun addPaginatedHistory(list: List<HistoryItem>)
        fun updateListAdapterSettings()
    }
}