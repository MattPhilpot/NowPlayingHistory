package com.philpot.nowplayinghistory.db2.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.philpot.nowplayinghistory.model.Song

@Dao
abstract class SongDao : GenericDao<Song>() {

    @Query("SELECT * FROM Song")
    abstract fun getAll(): List<Song>

    @Query("SELECT * FROM Song WHERE id = :id LIMIT 1")
    abstract fun getById(id: Int): Song?

    @Transaction
    open fun insertOrUpdate(model: Song) {
        insertOrUpdateInternal(model)
    }

    private fun insertOrUpdateInternal(model: Song) {
        if (getById(model.id) == null) {
            insert(model)
        } else {
            update(model)
        }
    }
}
