package com.philpot.nowplayinghistory.db2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.philpot.nowplayinghistory.db2.cipher.SQliteOpenHelperFactory
import com.philpot.nowplayinghistory.db2.converter.NowPlayingConverters
import com.philpot.nowplayinghistory.db2.dao.AlbumInfoDao
import com.philpot.nowplayinghistory.db2.dao.ArtistInfoDao
import com.philpot.nowplayinghistory.db2.dao.HistoryDao
import com.philpot.nowplayinghistory.db2.dao.ParameterDao
import com.philpot.nowplayinghistory.db2.dao.SongInfoDao
import com.philpot.nowplayinghistory.model.AlbumInfo
import com.philpot.nowplayinghistory.model.ArtistInfo
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.SongInfo
import com.philpot.nowplayinghistory.model.param.Parameter

@Database(entities =
[
    AlbumInfo::class,
    ArtistInfo::class,
    SongInfo::class,
    HistoryEntry::class,
    Parameter::class
],
    version = DatabaseVersion.DB_VERSION_1)
@TypeConverters(
    NowPlayingConverters::class
)
abstract class NowPlayingDatabase : RoomDatabase() {

    abstract fun albumDao(): AlbumInfoDao

    abstract fun artistDao(): ArtistInfoDao

    abstract fun songDao(): SongInfoDao

    abstract fun historyDao(): HistoryDao

    abstract fun parameterDao(): ParameterDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: NowPlayingDatabase? = null

        fun getInstance(context: Context): NowPlayingDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): NowPlayingDatabase {
            return Room
                .databaseBuilder(context, NowPlayingDatabase::class.java, "now_playing_history.db")
                .openHelperFactory(SQliteOpenHelperFactory(context))
                .build()
        }
    }
}
