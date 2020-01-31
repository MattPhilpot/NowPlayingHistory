package com.philpot.nowplayinghistory.db.dao

/**
 * Created by colse on 10/29/2017.
 */
interface DaoCache {
    fun <M, T> getDaoFor(modelClass: Class<M>): T
    //fun getAll(): Collection<Dao<*>>
}