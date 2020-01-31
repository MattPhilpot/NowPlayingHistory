package com.philpot.nowplayinghistory.db.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.philpot.nowplayinghistory.db.SQLiteTableBuilder
import com.philpot.nowplayinghistory.model.AlbumInfo
import com.philpot.nowplayinghistory.model.SongInfo
import com.pushtorefresh.storio.sqlite.StorIOSQLite
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery
import com.pushtorefresh.storio.sqlite.queries.InsertQuery
import com.pushtorefresh.storio.sqlite.queries.Query
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery

/**
 * Created by MattPhilpot on 11/9/2017.
 */
class AlbumInfoDao(storIO: StorIOSQLite) : GenericDao<AlbumInfo>(storIO, AlbumInfo::class.java, PutResolver(), GetResolver(), DeleteResolver()) {

    companion object {
        private val TABLE_NAME = "albuminfo"

        private val KEY_COL = "albumkey"
        private val ARTIST_COL = "artist"
        private val TITLE_COL = "title"
        private val YEAR_COL = "year"
        private val ALBUM_ART_CACHE_COL = "albumartpath"

        fun getAlbumKey(albumInfo: AlbumInfo): String = getAlbumKey(albumInfo.artist, albumInfo.title)

        fun getAlbumKey(artist: String, album: String): String = "$artist---$album"
    }

    override fun upgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //do nothing for now
    }

    override fun getTableName(): String = TABLE_NAME

    fun getAlbumInfoFrom(songInfo: SongInfo): AlbumInfo? {
        songInfo.album?.let {
            val query = Query.builder()
                    .table(TABLE_NAME)
                    .where(KEY_COL + WHERE_ARG_EQUALS)
                    .whereArgs(getAlbumKey(songInfo.artist, it))
                    .limit(1)
                    .build()

            return get(query).firstOrNull() ?: generateNewAlbumInfoFrom(songInfo.artist, it)
        }
        return null
    }

    private fun generateNewAlbumInfoFrom(artist: String, album: String): AlbumInfo {
        val retVal = AlbumInfo(title = album, artist = artist)
        insertOrUpdate(retVal)
        return retVal
    }

    override fun getCreateTableScript(dbVersion: Int): String {
        return SQLiteTableBuilder.builder()
                .tableName(TABLE_NAME)
                .textPrimaryKey(KEY_COL)
                .textColumn(ARTIST_COL)
                .textColumn(TITLE_COL)
                .textColumn(YEAR_COL)
                .textColumn(ALBUM_ART_CACHE_COL)
                .build()
    }

    class PutResolver : DefaultPutResolver<AlbumInfo>() {
        override fun mapToUpdateQuery(`object`: AlbumInfo): UpdateQuery {
            return UpdateQuery.builder()
                    .table(TABLE_NAME)
                    .where(KEY_COL + WHERE_ARG_EQUALS)
                    .whereArgs(getAlbumKey(`object`))
                    .build()
        }

        override fun mapToInsertQuery(`object`: AlbumInfo): InsertQuery {
            return InsertQuery.builder()
                    .table(TABLE_NAME)
                    .build()
        }

        override fun mapToContentValues(`object`: AlbumInfo): ContentValues {
            val retVal = ContentValues()
            retVal.put(KEY_COL, getAlbumKey(`object`))
            retVal.put(ARTIST_COL, `object`.artist)
            retVal.put(TITLE_COL, `object`.title)
            retVal.put(YEAR_COL, `object`.year)
            retVal.put(ALBUM_ART_CACHE_COL, `object`.albumArtPath)
            return retVal
        }
    }

    class GetResolver : DefaultGetResolver<AlbumInfo>() {
        override fun mapFromCursor(cursor: Cursor): AlbumInfo {
            return AlbumInfo(title = getString(cursor, TITLE_COL),
                    artist = getString(cursor, ARTIST_COL),
                    year = getString(cursor, YEAR_COL),
                    albumArtPath = getString(cursor, ALBUM_ART_CACHE_COL))
        }
    }

    class DeleteResolver : DefaultDeleteResolver<AlbumInfo>() {
        override fun mapToDeleteQuery(`object`: AlbumInfo): DeleteQuery {
            return DeleteQuery.builder()
                    .table(TABLE_NAME)
                    .build()
        }
    }
}