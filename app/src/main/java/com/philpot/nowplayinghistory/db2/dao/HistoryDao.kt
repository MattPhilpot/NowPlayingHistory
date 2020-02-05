package com.philpot.nowplayinghistory.db2.dao

import androidx.room.Dao
import com.philpot.nowplayinghistory.model.HistoryEntry

@Dao
abstract class HistoryDao : GenericDao<HistoryEntry>() {

}