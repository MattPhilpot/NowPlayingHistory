package com.philpot.nowplayinghistory.db.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.philpot.nowplayinghistory.db.SQLiteTableBuilder
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.model.HistoryItemLocation
import com.philpot.nowplayinghistory.model.SongInfo
import com.pushtorefresh.storio.sqlite.StorIOSQLite
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery
import com.pushtorefresh.storio.sqlite.queries.InsertQuery
import com.pushtorefresh.storio.sqlite.queries.Query
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery
import java.util.*

/**
 * Created by colse on 10/29/2017.
 */
class HistoryDao(storIO: StorIOSQLite) : GenericDao<HistoryItem>(storIO, HistoryItem::class.java, PutResolver(), GetResolver(), DeleteResolver()) {

    companion object {
        private val TABLE_NAME = "history"

        private val TITLE_COL = "title"
        private val ARTIST_COL = "artist"
        private val DATE_COL = "date"
        private val LATITUDE_COL = "latitude"
        private val LONGITUDE_COL = "longitude"
        private val ACCURACY_COL = "accuracy"

        /*
        val longitude: Double,
        val latitude: Double,
        val accuracy: Float
        private val SQL_UPGRADE_MOVE_1_TO_2 = "INSERT INTO history2(date, title, artist) SELECT date, title, artist FROM history"
        private val DROP_TABLE_1_TO_2 = "DROP TABLE history"
        private val SQL_UPGRADE_RENAME_1_TO_2 = "ALTER TABLE history2 RENAME TO history"
        */
    }

    override fun upgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        /*
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $LATITUDE_COL REAL")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $LONGITUDE_COL REAL")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $ACCURACY_COL REAL")
        }
        */
    }

    fun getHistoryOfSong(songInfo: SongInfo): List<HistoryItem> {
        val query = Query.builder()
                .table(TABLE_NAME)
                .where(TITLE_COL + WHERE_ARG_EQUALS_AND + ARTIST_COL + WHERE_ARG_EQUALS)
                .whereArgs(songInfo.title, songInfo.artist)
                .orderBy(DATE_COL + " DESC")
                .build()

        return get(query)
    }

    fun getLastNSongs(limit: Int, item: HistoryItem? = null): List<HistoryItem> {
        val query = Query.builder()
                .table(TABLE_NAME)
                .orderBy(DATE_COL + " DESC")
                .limit(limit)

        item?.let {
            query.where(TITLE_COL + WHERE_ARG_EQUALS_AND + ARTIST_COL + WHERE_ARG_EQUALS)
                    .whereArgs(it.title, it.artist)
        }

        return get(query.build())
    }

    fun insertIfNotRepeat(item: HistoryItem): Boolean {
        val query = Query.builder()
                .table(TABLE_NAME)
                .orderBy(DATE_COL + " DESC")
                .limit(1)
                .build()

        val list = get(query)
        if (list.isEmpty()) {
            insertOrUpdate(item)
            return true
        } else {
            val listItem = list[0]
            if (item.artist != listItem.artist || item.title != listItem.title) {
                insertOrUpdate(item)
                return true
            } else if ((item.timestamp - listItem.timestamp) > 600000) {
                //only update if it's been more than 10 minutes for the same song...
                insertOrUpdate(item)
                return true
            }
        }
        return false
    }

    fun getPaginatedHistoryList(pageSize: Int, startRecordKey: Long?): List<HistoryItem> {
        val query = Query.builder()
                .table(TABLE_NAME)
                .orderBy(DATE_COL + " DESC")
                .limit(pageSize)

        startRecordKey?.let {
            if (it > 0) {
                query.where(DATE_COL + WHERE_ARG_LESS_THAN).whereArgs(startRecordKey)
            }
        }

        return get(query.build())
    }

    fun getHistoryAfter(timeStamp: Long): List<HistoryItem> {
        val query = Query.builder()
                .table(TABLE_NAME)
                .orderBy(DATE_COL + " DESC")
                .where(DATE_COL + " > ?")
                .whereArgs(timeStamp)
                .build()

        return get(query)
    }

    /*
    fun getHistoryList(): List<HistoryItem> {
        val query = Query.builder()
                .table(TABLE_NAME)
                .orderBy(DATE_COL + " DESC")
                .build()
        return get(query)
    }
    */

    override fun getTableName(): String = TABLE_NAME

    override fun getCreateTableScript(dbVersion: Int): String {
        return SQLiteTableBuilder.builder()
                .tableName(TABLE_NAME)
                .integerPrimaryKey(DATE_COL)
                .textColumn(TITLE_COL)
                .textColumn(ARTIST_COL)
                .realColumn(LATITUDE_COL)
                .realColumn(LONGITUDE_COL)
                .realColumn(ACCURACY_COL)
                .build()
    }

    class PutResolver : DefaultPutResolver<HistoryItem>() {
        override fun mapToUpdateQuery(`object`: HistoryItem): UpdateQuery {
            return UpdateQuery.builder()
                    .table(TABLE_NAME)
                    .where(DATE_COL + WHERE_ARG_EQUALS)
                    .whereArgs(`object`.timestamp)
                    .build()
        }

        override fun mapToInsertQuery(`object`: HistoryItem): InsertQuery {
            return InsertQuery.builder()
                    .table(TABLE_NAME)
                    .build()
        }

        override fun mapToContentValues(`object`: HistoryItem): ContentValues {
            val retVal = ContentValues()
            retVal.put(DATE_COL, `object`.timestamp) //new key
            retVal.put(TITLE_COL, `object`.title)
            retVal.put(ARTIST_COL, `object`.artist)

            `object`.location?.let {
                retVal.put(LATITUDE_COL, it.latitude)
                retVal.put(LONGITUDE_COL, it.longitude)
                retVal.put(ACCURACY_COL, it.accuracy)
            }

            return retVal
        }
    }

    class GetResolver : DefaultGetResolver<HistoryItem>() {
        override fun mapFromCursor(cursor: Cursor): HistoryItem {
            val latitude = getDoubleNullable(cursor, LATITUDE_COL)
            val longitude = getDoubleNullable(cursor, LONGITUDE_COL)
            val accuracy = getFloatNullable(cursor, ACCURACY_COL)

            return HistoryItem(title = getString(cursor, TITLE_COL),
                    artist = getString(cursor, ARTIST_COL),
                    timestamp = getLong(cursor, DATE_COL),
                    location = getPossibleLocation(latitude, longitude, accuracy))
        }

        private fun getPossibleLocation(latitude: Double?, longitude: Double?, accuracy: Float?): HistoryItemLocation? {
            if (latitude != null && longitude != null && accuracy != null) {
                return HistoryItemLocation(latitude, longitude, accuracy)
            }
            return null
        }
    }

    class DeleteResolver : DefaultDeleteResolver<HistoryItem>() {
        override fun mapToDeleteQuery(`object`: HistoryItem): DeleteQuery {
            return DeleteQuery.builder()
                    .table(TABLE_NAME)
                    .where(DATE_COL + WHERE_ARG_EQUALS)
                    .whereArgs(`object`.timestamp)
                    .build()
        }
    }
}