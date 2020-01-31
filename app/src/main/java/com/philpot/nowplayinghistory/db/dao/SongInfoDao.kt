package com.philpot.nowplayinghistory.db.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.philpot.nowplayinghistory.db.SQLiteTableBuilder
import com.philpot.nowplayinghistory.model.SongInfo
import com.philpot.nowplayinghistory.model.HistoryItem
import com.pushtorefresh.storio.sqlite.StorIOSQLite
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery
import com.pushtorefresh.storio.sqlite.queries.InsertQuery
import com.pushtorefresh.storio.sqlite.queries.Query
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery

/**
 * Created by MattPhilpot on 11/6/2017.
 */
class SongInfoDao(storIO: StorIOSQLite) : GenericDao<SongInfo>(storIO, SongInfo::class.java, PutResolver(), GetResolver(), DeleteResolver()) {

    companion object {
        private val TABLE_NAME = "lastheard"

        private val KEY_COL = "key"
        private val ARTIST_COL = "artist"
        private val TITLE_COL = "title"
        private val ALBUM_COL = "album"
        private val LAST_HEARD_COL = "lastheard"
        private val HEARD_COUNT_COL = "heardcount"
        private val CURRENT_HEARD_COL = "currentheard"
        private val FAVORITE_COL = "favorite"
        private val IS_EXPANDED_COL = "expanded"

        fun getSongInfoKey(item: SongInfo): String = "${item.artist}---${item.title}"

        fun getSongInfoKey(item: HistoryItem): String = "${item.artist}---${item.title}"

        private val INSERT_UPGRADE_3_TO_4 = "INSERT INTO lastheard SELECT " +
                " b.artist || '---' || b.title as 'key', " +
                " b.title, " +
                " b.artist, " +
                " '' as 'album', " +
                " (SELECT MAX(date) FROM history a WHERE a.artist = b.artist AND a.title = b.title) as 'currentheard', " +
                " ifnull((SELECT MAX(date) FROM history a WHERE a.artist = b.artist AND a.title = b.title AND a.date < (SELECT MAX(date) FROM history a WHERE a.artist = b.artist AND a.title = b.title)), (SELECT MAX(date) FROM history a WHERE a.artist = b.artist AND a.title = b.title)) as 'lastheard', " +
                " COUNT(*) as 'heardcount', " +
                " 0 as 'favorite'" +
                " FROM history b" +
                " GROUP BY b.title, b.artist"
    }


    override fun upgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        /*
        if (oldVersion < 6) { //"$artist---$album"
            //db.execSQL("DELETE FROM albuminfo WHERE albumkey IN (SELECT lastheard.artist || '---' || lastheard.album FROM lastheard WHERE lastheard.title LIKE '%(%')")
            db.execSQL("DELETE FROM $TABLE_NAME")
            db.execSQL(INSERT_UPGRADE_3_TO_4)
        }
        */
        if (oldVersion < 8) {
            db.execSQL("ALTER TABLE $TABLE_NAME RENAME TO lastheard2")
            db.execSQL(getCreateTableScript(newVersion))
            db.execSQL("INSERT INTO $TABLE_NAME($KEY_COL, $ARTIST_COL, $TITLE_COL, $ALBUM_COL, $LAST_HEARD_COL, $CURRENT_HEARD_COL, $FAVORITE_COL, $IS_EXPANDED_COL) " +
                    "SELECT  $KEY_COL, $ARTIST_COL, $TITLE_COL, $ALBUM_COL, $LAST_HEARD_COL, $CURRENT_HEARD_COL, $FAVORITE_COL, 0 FROM lastheard2")
            db.execSQL("DROP TABLE lastheard2")
            //db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $IS_EXPANDED_COL INTEGER NOT NULL DEFAULT 0")
        }
    }

    fun getFavorites(): List<SongInfo> {
        val query = Query.builder()
                .table(TABLE_NAME)
                .where(FAVORITE_COL + " = 1")
                .build()

        return get(query)
    }

    fun updateLastHeard(item: HistoryItem) {
        val updatedItem = getSongInfoFrom(item)
        updatedItem.lastHeard = updatedItem.currentHeard //set last to what "was" the current
        updatedItem.currentHeard = item.timestamp //update current to the new timestamp
        if (updatedItem.lastHeard != item.timestamp) { //if last != current, means it was a new session, so increment count by 1
            updatedItem.heardCount = updatedItem.heardCount + 1
        }
        insertOrUpdate(updatedItem)
    }

    fun getSongInfoFrom(item: HistoryItem): SongInfo {
        val query = Query.builder()
                .table(TABLE_NAME)
                .where(KEY_COL + WHERE_ARG_EQUALS)
                .whereArgs(getSongInfoKey(item))
                .limit(1)
                .build()

        return get(query).firstOrNull() ?: generateNewSongInfoFrom(item.copy())
    }

    private fun generateNewSongInfoFrom(item: HistoryItem): SongInfo {
        val retVal = SongInfo(title = item.title,
                artist = item.artist,
                lastHeard = item.timestamp,
                currentHeard = item.timestamp,
                heardCount = 1,
                favorite = false,
                isExpanded = false)
        insertOrUpdate(retVal)
        return retVal
    }

    override fun getTableName(): String = TABLE_NAME

    override fun getCreateTableScript(dbVersion: Int): String {
        return SQLiteTableBuilder.builder()
                .tableName(TABLE_NAME)
                .textPrimaryKey(KEY_COL)
                .textColumn(TITLE_COL)
                .textColumn(ARTIST_COL)
                .textColumn(ALBUM_COL)
                .intColumn(CURRENT_HEARD_COL)
                .intColumn(LAST_HEARD_COL)
                .intColumn(HEARD_COUNT_COL)
                .booleanColumn(FAVORITE_COL)
                .booleanColumn(IS_EXPANDED_COL)
                .build()
    }

    class PutResolver : DefaultPutResolver<SongInfo>() {
        override fun mapToUpdateQuery(`object`: SongInfo): UpdateQuery {
            return UpdateQuery.builder()
                    .table(TABLE_NAME)
                    .where(KEY_COL + WHERE_ARG_EQUALS)
                    .whereArgs(getSongInfoKey(`object`))
                    .build()
        }

        override fun mapToInsertQuery(`object`: SongInfo): InsertQuery {
            return InsertQuery.builder()
                    .table(TABLE_NAME)
                    .build()
        }

        override fun mapToContentValues(`object`: SongInfo): ContentValues {
            val retVal = ContentValues()
            retVal.put(KEY_COL, getSongInfoKey(`object`))
            retVal.put(TITLE_COL, `object`.title)
            retVal.put(ARTIST_COL, `object`.artist)
            retVal.put(ALBUM_COL, `object`.album)
            retVal.put(LAST_HEARD_COL, `object`.lastHeard)
            retVal.put(CURRENT_HEARD_COL, `object`.currentHeard)
            retVal.put(HEARD_COUNT_COL, `object`.heardCount)
            retVal.put(FAVORITE_COL, `object`.favorite)
            retVal.put(IS_EXPANDED_COL, `object`.isExpanded)
            return retVal
        }
    }

    class GetResolver : DefaultGetResolver<SongInfo>() {
        override fun mapFromCursor(cursor: Cursor): SongInfo {
            return SongInfo(title = getString(cursor, TITLE_COL),
                    artist = getString(cursor, ARTIST_COL),
                    album = getString(cursor, ALBUM_COL),
                    lastHeard = getLong(cursor, LAST_HEARD_COL),
                    currentHeard = getLong(cursor, CURRENT_HEARD_COL),
                    heardCount = getLong(cursor, HEARD_COUNT_COL),
                    favorite = getBoolean(cursor, FAVORITE_COL),
                    isExpanded = getBoolean(cursor, IS_EXPANDED_COL))
        }
    }

    class DeleteResolver : DefaultDeleteResolver<SongInfo>() {
        override fun mapToDeleteQuery(`object`: SongInfo): DeleteQuery {
            return DeleteQuery.builder()
                    .table(TABLE_NAME)
                    .build()
        }
    }
}