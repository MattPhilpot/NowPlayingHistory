package com.philpot.nowplayinghistory.db2.dao

import androidx.room.Dao
import androidx.room.Query
import com.philpot.nowplayinghistory.model.Artist

@Dao
abstract class ArtistDao : GenericDao<Artist>() {

    @Query("SELECT * FROM Artist")
    abstract fun getAll(): List<Artist>

    @Query("SELECT * FROM Artist WHERE name = :name LIMIT 1")
    abstract fun getByName(name: String): Artist?
}
