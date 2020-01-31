package com.philpot.nowplayinghistory.db.dao

import android.database.sqlite.SQLiteDatabase
import com.pushtorefresh.storio.sqlite.operations.put.PutResult
import com.pushtorefresh.storio.sqlite.queries.Query
import com.pushtorefresh.storio.sqlite.queries.RawQuery

/**
 * Created by colse on 10/29/2017.
 */
interface Dao<M> {
    fun create(db: SQLiteDatabase)

    fun upgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)

    fun getTableName(): String

    operator fun get(query: Query): List<M>

    fun getNumberOfResults(query: Query): Int?

    fun getAll(): List<M>

    fun insertOrUpdate(model: M): PutResult

    fun insertOrUpdateAll(models: Collection<M>): List<PutResult>

    fun delete(model: M)

    //fun clearTable()

    fun getCount(): Int
}