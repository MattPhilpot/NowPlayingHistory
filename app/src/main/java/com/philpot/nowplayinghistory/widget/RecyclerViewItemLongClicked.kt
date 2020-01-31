package com.philpot.nowplayinghistory.widget

/**
 * Created by MattPhilpot on 10/30/2017.
 */
interface RecyclerViewItemLongClicked<E> {
    fun itemLongClicked(model: E?, holder: RecyclerViewHolder<E>): Boolean
}