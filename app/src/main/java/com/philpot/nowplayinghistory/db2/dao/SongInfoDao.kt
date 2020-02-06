package com.philpot.nowplayinghistory.db2.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.philpot.nowplayinghistory.model.SongInfo

@Dao
abstract class SongInfoDao : GenericDao<SongInfo>() {

    @Query("SELECT * FROM SongInfo")
    abstract fun getAll(): List<SongInfo>

    @Query("SELECT * FROM SongInfo WHERE id = :id LIMIT 1")
    abstract fun getById(id: Long): SongInfo?

    @Query("SELECT * FROM SongInfo WHERE title = :title AND artist = :artist LIMIT 1")
    abstract fun getByTitleAndArtist(title: String, artist: String): SongInfo?

    @Transaction
    open fun insertOrUpdate(model: SongInfo): Long {
        return insertOrUpdateInternal(model)
    }

    private fun insertOrUpdateInternal(model: SongInfo): Long {
        val existing = getByTitleAndArtist(model.title, model.artist)
        return if (existing == null) {
            insert(model)
        } else {
            ++existing.heardCount
            update(existing)
            existing.id
        }
    }
}
