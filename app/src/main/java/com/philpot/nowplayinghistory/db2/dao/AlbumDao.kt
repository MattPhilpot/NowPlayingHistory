package com.philpot.nowplayinghistory.db2.dao

import androidx.room.Dao
import com.philpot.nowplayinghistory.model.Album

@Dao
abstract class AlbumDao : GenericDao<Album>() {

}