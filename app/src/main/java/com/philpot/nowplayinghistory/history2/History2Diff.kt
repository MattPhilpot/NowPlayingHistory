package com.philpot.nowplayinghistory.history2

import androidx.recyclerview.widget.DiffUtil
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.HistoryItem

class History2Diff : DiffUtil.ItemCallback<HistoryItem>() {

    override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
        return oldItem.historyEntry.id == newItem.historyEntry.id
    }

    override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
        return oldItem.historyEntry.id == newItem.historyEntry.id
    }
}
