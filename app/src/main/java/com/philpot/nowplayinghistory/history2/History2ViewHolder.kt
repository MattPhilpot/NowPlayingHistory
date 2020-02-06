package com.philpot.nowplayinghistory.history2

import com.philpot.nowplayinghistory.databinding.ViewholderHistoryItemBinding
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.viewmodel.HistoryEntryViewModel
import com.philpot.nowplayinghistory.widget.recycler.RecyclerViewHolder

class History2ViewHolder(private val binding: ViewholderHistoryItemBinding) : RecyclerViewHolder<HistoryItem>(binding.root) {

    override fun updateView(newEntity: HistoryItem?) {
        with(binding) {
            viewModel = HistoryEntryViewModel(newEntity)
            //binding.setVariable(BR.viewModel, )
            executePendingBindings()
        }
    }
}
