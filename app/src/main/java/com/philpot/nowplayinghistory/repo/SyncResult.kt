package com.philpot.nowplayinghistory.repo

/**
 * Has support for local or remote loading.
 */
data class SyncResult<out T>(val status: Status,
                             val data: T?,
                             val message: String) {

    enum class Status {
        SUCCESS_LOCAL,
        SUCCESS_REMOTE,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> successLocal(data: T): SyncResult<T> {
            return SyncResult(
                Status.SUCCESS_LOCAL,
                data,
                ""
            )
        }

        fun <T> successRemote(data: T): SyncResult<T> {
            return SyncResult(
                Status.SUCCESS_REMOTE,
                data,
                ""
            )
        }

        fun <T> error(message: String, data: T? = null): SyncResult<T> {
            return SyncResult(
                Status.ERROR,
                data,
                message
            )
        }

        fun <T> loading(data: T? = null): SyncResult<T> {
            return SyncResult(
                Status.LOADING,
                data,
                ""
            )
        }
    }
}
