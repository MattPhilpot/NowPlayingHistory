package com.philpot.nowplayinghistory.db2.dao

import androidx.room.*

@Dao
abstract class GenericDao<E> {

    //basic methods already implemented by this class
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(model: E)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertAll(vararg model: E)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertAll(models: List<E>)

    @Update
    abstract fun update(model: E)

    @Delete
    abstract fun delete(model: E)
}