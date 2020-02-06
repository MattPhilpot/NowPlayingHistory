package com.philpot.nowplayinghistory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.philpot.nowplayinghistory.repo.SyncResult

abstract class RefreshableViewModel<T> : ViewModel() {

    private val reload = MutableLiveData<Boolean>()
    private val dataInternal: LiveData<SyncResult<T>> = Transformations.switchMap(reload) {
        getData()
    }

    init {
        refresh()
    }

    fun data(): LiveData<SyncResult<T>> = dataInternal

    protected abstract fun getData(): LiveData<SyncResult<T>>

    fun refresh() {
        reload.value = true
    }
}
