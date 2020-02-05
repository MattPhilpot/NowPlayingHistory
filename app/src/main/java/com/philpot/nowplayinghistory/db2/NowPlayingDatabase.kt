package com.philpot.nowplayinghistory.db2

import androidx.room.Database
import androidx.room.RoomDatabase
import com.philpot.nowplayinghistory.db2.dao.AlbumDao
import com.philpot.nowplayinghistory.db2.dao.ArtistDao
import com.philpot.nowplayinghistory.db2.dao.HistoryDao
import com.philpot.nowplayinghistory.db2.dao.SongDao
import com.philpot.nowplayinghistory.model.Album
import com.philpot.nowplayinghistory.model.Artist
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.Song

@Database(entities =
[
    Album::class,
    Artist::class,
    Song::class,
    HistoryEntry::class
],
    version = DatabaseVersion.DB_VERSION_1)
abstract class NowPlayingDatabase : RoomDatabase() {

    abstract fun albumDao(): AlbumDao

    abstract fun artistDao(): ArtistDao

    abstract fun songDao(): SongDao

    abstract fun historyDao(): HistoryDao
}
