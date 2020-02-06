package com.philpot.nowplayinghistory.widget.recycler

interface RecyclerViewItemClicked<E> {
    fun itemClicked(model: E?, holder: RecyclerViewHolder<E>)
}
