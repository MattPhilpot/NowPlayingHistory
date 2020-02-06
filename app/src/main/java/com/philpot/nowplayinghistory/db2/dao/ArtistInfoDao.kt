package com.philpot.nowplayinghistory.db2.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.philpot.nowplayinghistory.model.ArtistInfo

@Dao
abstract class ArtistInfoDao : GenericDao<ArtistInfo>() {

    @Query("SELECT * FROM ArtistInfo")
    abstract fun getAll(): List<ArtistInfo>

    @Query("SELECT * FROM ArtistInfo WHERE name = :name LIMIT 1")
    abstract fun getByName(name: String): ArtistInfo?

    @Transaction
    open fun insertOrUpdate(model: ArtistInfo) {
        return insertOrUpdateInternal(model)
    }

    private fun insertOrUpdateInternal(model: ArtistInfo) {
        if (getByName(model.name) == null) {
            insert(model)
        } else {
            update(model)
        }
    }
}
