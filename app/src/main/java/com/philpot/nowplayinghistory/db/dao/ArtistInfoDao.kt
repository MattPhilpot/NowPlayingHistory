package com.philpot.nowplayinghistory.db.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.philpot.nowplayinghistory.model.ArtistInfo
import com.pushtorefresh.storio.sqlite.StorIOSQLite
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery
import com.pushtorefresh.storio.sqlite.queries.InsertQuery
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery

/**
 * Created by MattPhilpot on 12/13/2017.
 */
class ArtistInfoDao(storIO: StorIOSQLite) : GenericDao<ArtistInfo>(storIO, ArtistInfo::class.java, PutResolver(), GetResolver(), DeleteResolver()) {

    companion object {
        val TABLE_NAME = "artistinfo"

        private val NAME_COL = "name"
        private val INFO_COL = "info"
        private val ARTIST_ART_CACHE_COL = "artistartpath"
    }

    override fun upgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTableName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCreateTableScript(dbVersion: Int): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class PutResolver : DefaultPutResolver<ArtistInfo>() {
        override fun mapToUpdateQuery(`object`: ArtistInfo): UpdateQuery {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun mapToContentValues(`object`: ArtistInfo): ContentValues {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun mapToInsertQuery(`object`: ArtistInfo): InsertQuery {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    class GetResolver : DefaultGetResolver<ArtistInfo>() {
        override fun mapFromCursor(cursor: Cursor): ArtistInfo {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    class DeleteResolver : DefaultDeleteResolver<ArtistInfo>() {
        override fun mapToDeleteQuery(`object`: ArtistInfo): DeleteQuery {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}