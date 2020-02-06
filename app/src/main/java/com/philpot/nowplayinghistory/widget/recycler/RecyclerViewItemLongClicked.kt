package com.philpot.nowplayinghistory.widget.recycler

interface RecyclerViewItemLongClicked<E> {
    fun itemLongClicked(model: E?, holder: RecyclerViewHolder<E>): Boolean
}
