package com.philpot.nowplayinghistory.db2.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.philpot.nowplayinghistory.model.AlbumInfo

@Dao
abstract class AlbumInfoDao : GenericDao<AlbumInfo>() {

    @Query("SELECT * FROM AlbumInfo WHERE artist = :artist AND title = :title LIMIT 1")
    abstract fun getByArtistAndTitle(artist: String, title: String): AlbumInfo?

    @Transaction
    open fun insertOrUpdate(model: AlbumInfo): Long {
        return insertOrUpdateInternal(model)
    }

    private fun insertOrUpdateInternal(model: AlbumInfo): Long {
        val existing = getByArtistAndTitle(model.title, model.artist)
        return if (existing == null) {
            insert(model)
        } else {
            update(existing)
            existing.id
        }
    }
}