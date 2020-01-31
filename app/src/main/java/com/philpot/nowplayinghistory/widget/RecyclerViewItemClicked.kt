package com.philpot.nowplayinghistory.widget

/**
 * Created by MattPhilpot on 10/30/2017.
 */
interface RecyclerViewItemClicked<E> {
    fun itemClicked(model: E?, holder: RecyclerViewHolder<E>)
}