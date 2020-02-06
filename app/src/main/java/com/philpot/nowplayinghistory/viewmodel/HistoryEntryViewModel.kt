package com.philpot.nowplayinghistory.viewmodel

import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.util.Utils

class HistoryEntryViewModel(val historyItem: HistoryItem?) {

    val title: String
        get() = historyItem?.songInfo?.title ?: ""

    val favorite: Boolean
        get() = historyItem?.songInfo?.favorite ?: false

    val artist: String
        get() = historyItem?.songInfo?.artist ?: ""

    val date: String
        get() = Utils.formatDateTime(historyItem?.historyEntry?.timestamp?.millis)

    val imageUrl: String
        get() = historyItem?.albumInfo?.albumArtPath ?: ""

    val lastTimeHeard: String
        get() = ""//lastEntry?.timestamp?.toString() ?: ""

}
