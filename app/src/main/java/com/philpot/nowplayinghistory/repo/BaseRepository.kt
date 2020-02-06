package com.philpot.nowplayinghistory.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.philpot.nowplayinghistory.coroutine.CoroutineContextProvider

abstract class BaseRepository(protected val coroutineContextProvider: CoroutineContextProvider) {

    fun <T, A> resultLiveData(databaseQuery: () -> LiveData<T>,
                              networkCall: suspend () -> SyncResult<A>,
                              saveCallResult: suspend (A?) -> LiveData<SyncResult<T>>
    ): LiveData<SyncResult<T>> =
        liveData(coroutineContextProvider.ioContext) {
            emit(SyncResult.loading())
            val source = databaseQuery.invoke().map { SyncResult.successLocal(it) }
            emitSource(source)

            val responseStatus = networkCall.invoke()
            if (responseStatus.status == SyncResult.Status.SUCCESS_REMOTE) {
                emitSource(saveCallResult(responseStatus.data))
            } else if (responseStatus.status == SyncResult.Status.ERROR) {
                emit(SyncResult.error(responseStatus.message))
                emitSource(source)
            }
        }
}
