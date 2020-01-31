package com.philpot.nowplayinghistory.favorite

import android.content.SharedPreferences
import com.philpot.nowplayinghistory.db.dao.HistoryDao
import com.philpot.nowplayinghistory.db.dao.SongInfoDao
import com.philpot.nowplayinghistory.model.SongInfo

/**
 * Created by MattPhilpot on 12/2/2017.
 */
class FavoriteController(private var view: FavoriteView?,
                         private val songInfoDao: SongInfoDao,
                         private val preferences: SharedPreferences) {
    fun initialize() {
        //do nothing for now perhaps
    }

    fun onResume() {
        loadFavorites()
    }

    private fun loadFavorites() {
        Runnable {
            val list = songInfoDao.getFavorites()
            view?.updateFavorites(list)
        }.run()
    }

    interface FavoriteView {
        fun updateFavorites(list: List<SongInfo>)
    }
}