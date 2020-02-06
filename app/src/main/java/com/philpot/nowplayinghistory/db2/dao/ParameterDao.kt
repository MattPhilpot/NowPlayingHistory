package com.philpot.nowplayinghistory.db2.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.philpot.nowplayinghistory.model.param.Parameter

@Dao
abstract class ParameterDao : GenericDao<Parameter>() {

    @Query("SELECT * FROM parameters")
    abstract fun getAll(): List<Parameter>

    //the ID in parameter is the code of the parameterType
    @Query("SELECT * FROM parameters WHERE id = :key")
    abstract fun getById(key: String): Parameter?

    @Transaction
    open fun insertOrUpdate(model: Parameter) {
        insertOrUpdateInternal(model)
    }

    @Transaction
    open fun insertOrUpdateAll(vararg models: Parameter) {
        models.forEach {
            insertOrUpdateInternal(it)
        }
    }

    @Transaction
    open fun insertOrUpdateAll(models: List<Parameter>) {
        models.forEach {
            insertOrUpdateInternal(it)
        }
    }

    private fun insertOrUpdateInternal(model: Parameter) {
        if (getById(model.id) == null) {
            insert(model)
        } else {
            update(model)
        }
    }
}
