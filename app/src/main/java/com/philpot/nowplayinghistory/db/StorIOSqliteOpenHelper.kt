package com.philpot.nowplayinghistory.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.philpot.nowplayinghistory.db.dao.*
import com.philpot.nowplayinghistory.model.AlbumInfo
import com.philpot.nowplayinghistory.model.SongInfo
import com.philpot.nowplayinghistory.model.HistoryItem
import com.pushtorefresh.storio.sqlite.BuildConfig
import com.pushtorefresh.storio.sqlite.StorIOSQLite
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite

/**
 * Created by colse on 10/29/2017.
 */
class StorIOSqliteOpenHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), DaoCache {

    companion object {
        private val TAG = StorIOSqliteOpenHelper::class.java.simpleName
        private val DATABASE_NAME = "nowplaying"
        private val DATABASE_VERSION = 8
    }

    private val storIOSQLite : StorIOSQLite
    private val daos: HashMap<Class<*>, Dao<*>>

    init {
        val completeBuilder = DefaultStorIOSQLite.builder().sqliteOpenHelper(this)
        storIOSQLite = completeBuilder.build()
        daos = initializeDaos()
    }

    override fun onCreate(db: SQLiteDatabase) {
        doDaoCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        doDaoUpgrade(db, oldVersion, newVersion)
    }

    private fun doDaoCreate(db: SQLiteDatabase) {
        val createdTables = arrayListOf<String>()

        //doing this so that we can give it multiple chances
        for (i in 0..2) {
            for (dao in daos.values) {
                try {
                    if (!createdTables.contains(dao.getTableName())) {
                        dao.create(db)
                        createdTables.add(dao.getTableName())
                    }
                } catch (t: Throwable) {
                    Log.e(TAG, "failed creating table " + dao.getTableName(), t)
                }
            }

            if (createdTables.size == daos.size) {
                return //no reason to try again since all tables created fine
            }
        }
    }

    private fun doDaoUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val updatedTables = arrayListOf<String>()

        for (dao in daos.values) {
            if (!updatedTables.contains(dao.getTableName())) {
                dao.upgrade(db, oldVersion, newVersion)
                updatedTables.add(dao.getTableName())
            }
        }
    }

    fun getStorIO(): StorIOSQLite {
        return storIOSQLite
    }

    override fun <M, T> getDaoFor(modelClass: Class<M>): T {
        @Suppress("UNCHECKED_CAST")
        return daos[modelClass] as T
    }

    private fun initializeDaos(): HashMap<Class<*>, Dao<*>> {
        val retVal = HashMap<Class<*>, Dao<*>>()
        retVal.put(HistoryItem::class.java, HistoryDao(storIOSQLite))
        retVal.put(SongInfo::class.java, SongInfoDao(storIOSQLite))
        retVal.put(AlbumInfo::class.java, AlbumInfoDao(storIOSQLite))
        return retVal
    }
}