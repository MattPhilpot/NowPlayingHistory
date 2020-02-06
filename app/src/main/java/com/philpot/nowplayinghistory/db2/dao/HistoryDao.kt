package com.philpot.nowplayinghistory.db2.dao

import androidx.annotation.IntRange
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.HistoryItem

@Dao
abstract class HistoryDao : GenericDao<HistoryEntry>() {

    @Query("SELECT * FROM HistoryEntries WHERE id = :id LIMIT 1")
    abstract fun getById(id: Long): HistoryEntry?

    @Query("SELECT * FROM HistoryEntries ORDER BY timestamp DESC LIMIT :count")
    abstract fun getMostRecentItems(@IntRange(from = 1) count: Int): List<HistoryEntry>

    @Query("SELECT * FROM HistoryEntries ORDER BY timestamp DESC")
    abstract fun getHistory(): DataSource.Factory<Int, HistoryItem>

    @Transaction
    open fun insertIfNotRepeat(model: HistoryEntry): Long? {
        val lastModel = getMostRecentItems(1).firstOrNull()
        return if (lastModel == null ||
                lastModel.songId != model.songId ||
                lastModel.timestamp.plus(TIME_SEPARATOR).isBefore(model.timestamp)) {
            insert(model)
        } else {
            null
        }
    }

    @Transaction
    open fun insertOrUpdate(model: HistoryEntry): Long {
        return insertOrUpdateInternal(model)
    }

    @Transaction
    open fun insertOrUpdateAll(models: List<HistoryEntry>) {
        models.forEach {
            insertOrUpdateInternal(it)
        }
    }

    private fun insertOrUpdateInternal(model: HistoryEntry): Long {
        val existing = getById(model.id)
        return if (existing == null || model.timestamp.millis != existing.timestamp.millis) {
            insert(model)
        } else {
            update(existing)
            existing.id
        }
    }

    companion object {
        private const val TIME_SEPARATOR = 600000L
    }
}
