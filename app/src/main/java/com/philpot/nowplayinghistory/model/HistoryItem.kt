package com.philpot.nowplayinghistory.model

import java.util.*

/**
 * Created by colse on 10/29/2017.
 */
data class HistoryItem(var title: String,
                       var artist: String,
                       var timestamp: Long,
                       var location: HistoryItemLocation? = null,
                       var songInfo: SongInfo? = null) {
    fun getDateFromTimestamp(): Date = Date(timestamp)
}