package com.philpot.nowplayinghistory.db.dao

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery
import com.pushtorefresh.storio.sqlite.operations.put.PutResult
import com.pushtorefresh.storio.sqlite.queries.RawQuery
import android.text.TextUtils
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.database.Cursor
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver
import com.pushtorefresh.storio.sqlite.StorIOSQLite
import com.pushtorefresh.storio.sqlite.queries.Query

/**
 * Created by colse on 10/29/2017.
 */
abstract class GenericDao<E>(protected val storIo: StorIOSQLite,
                             private val modelClass: Class<E>,
                             private val putResolver: PutResolver<E>,
                             private val getResolver: GetResolver<E>,
                             private val deleteResolver: DeleteResolver<E>) : Dao<E> {

    companion object {
        val WHERE_ARG_EQUALS = " = ?"
        val WHERE_ARG_EQUALS_AND = " = ? AND "
        val WHERE_ARG_EQUALS_OR = " = ? OR "
        val WHERE_ARG_LESS_THAN = " < ?"


        fun getString(cursor: Cursor, colName: String): String {
            var retVal: String = ""
            val columnIndexOrThrow = cursor.getColumnIndexOrThrow(colName)
            if (!cursor.isNull(columnIndexOrThrow)) {
                retVal = cursor.getString(columnIndexOrThrow)
            }
            return retVal
        }

        fun getBoolean(cursor: Cursor, colName: String, default: Boolean = false): Boolean {
            var retVal: Boolean? = null
            val columnIndexOrThrow = cursor.getColumnIndexOrThrow(colName)
            if (!cursor.isNull(columnIndexOrThrow)) {
                retVal = cursor.getShort(columnIndexOrThrow).toInt() == 1
            }
            return retVal ?: default
        }



        fun getInt(cursor: Cursor, colName: String, default: Int = 0): Int {
            var retVal: Int? = null
            val columnIndexOrThrow = cursor.getColumnIndexOrThrow(colName)
            if (!cursor.isNull(columnIndexOrThrow)) {
                retVal = cursor.getInt(columnIndexOrThrow)
            }
            return retVal ?: default
        }

        fun getDouble(cursor: Cursor, colName: String, default: Double = 0.0): Double {
            var retVal: Double? = null
            val columnIndexOrThrow = cursor.getColumnIndexOrThrow(colName)
            if (!cursor.isNull(columnIndexOrThrow)) {
                retVal = cursor.getDouble(columnIndexOrThrow)
            }
            return retVal ?: default
        }

        fun getDoubleNullable(cursor: Cursor, colName: String): Double? {
            var retVal: Double? = null
            val columnIndexOrThrow = cursor.getColumnIndexOrThrow(colName)
            if (!cursor.isNull(columnIndexOrThrow)) {
                retVal = cursor.getDouble(columnIndexOrThrow)
            }
            return retVal
        }

        fun getFloat(cursor: Cursor, colName: String, default: Float = 0.0F): Float {
            var retVal: Float? = null
            val columnIndexOrThrow = cursor.getColumnIndexOrThrow(colName)
            if (!cursor.isNull(columnIndexOrThrow)) {
                retVal = cursor.getFloat(columnIndexOrThrow)
            }
            return retVal ?: default
        }

        fun getFloatNullable(cursor: Cursor, colName: String): Float? {
            var retVal: Float? = null
            val columnIndexOrThrow = cursor.getColumnIndexOrThrow(colName)
            if (!cursor.isNull(columnIndexOrThrow)) {
                retVal = cursor.getFloat(columnIndexOrThrow)
            }
            return retVal
        }

        fun getLong(cursor: Cursor, colName: String, default: Long = 0L): Long {
            var retVal: Long? = null
            val columnIndexOrThrow = cursor.getColumnIndexOrThrow(colName)
            if (!cursor.isNull(columnIndexOrThrow)) {
                retVal = cursor.getLong(columnIndexOrThrow)
            }
            return retVal ?: default
        }

        fun putBoolean(key: String, boolValue: Boolean, contentValues: ContentValues) {
            contentValues.put(key, if (boolValue) 1 else 0)
        }
    }

    override fun create(db: SQLiteDatabase) {
        val createTableScript = getCreateTableScript(db.version)
        if (!TextUtils.isEmpty(createTableScript)) {
            db.execSQL(createTableScript)
        }
    }

    protected abstract fun getCreateTableScript(dbVersion: Int): String

    override fun get(query: Query): List<E> {
        return storIo.get()
                .listOfObjects(modelClass)
                .withQuery(query)
                .withGetResolver(getResolver)
                .prepare()
                .executeAsBlocking()
    }

    override fun getNumberOfResults(query: Query): Int? {
        return storIo.get()
                .numberOfResults()
                .withQuery(query)
                .prepare()
                .executeAsBlocking()
    }

    override fun getAll(): List<E> {
        return storIo.get()
                .listOfObjects(modelClass)
                .withQuery(Query.builder().table(getTableName()).build())
                .withGetResolver(getResolver)
                .prepare()
                .executeAsBlocking()
    }

    /*
    override fun clearTable() {
        val deleteQuery = DeleteQuery.builder().table(getTableName()).build()
        storIo.delete().byQuery(deleteQuery).prepare().executeAsBlocking()
    }
    */

    override fun insertOrUpdate(model: E): PutResult {
        return storIo.put()
                .`object`(model)
                .withPutResolver(putResolver)
                .prepare()
                .executeAsBlocking()
    }

    override fun insertOrUpdateAll(models: Collection<E>): List<PutResult> = models.map { insertOrUpdate(it) }

    override fun delete(model: E) {
        storIo.delete()
                .`object`(model)
                .withDeleteResolver(deleteResolver)
                .prepare()
                .executeAsBlocking()
    }

    protected fun delete(deleteQuery: DeleteQuery) {
        storIo.delete()
                .byQuery(deleteQuery)
                .prepare()
                .executeAsBlocking()
    }

    override fun getCount(): Int {
        val query = Query.builder()
                .table(getTableName())
                .build()
        return storIo.get()
                .numberOfResults()
                .withQuery(query)
                .prepare()
                .executeAsBlocking()
    }
}