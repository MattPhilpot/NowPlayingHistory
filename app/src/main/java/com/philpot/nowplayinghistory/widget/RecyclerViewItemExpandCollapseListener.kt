package com.philpot.nowplayinghistory.widget

/**
 * Created by MattPhilpot on 12/13/2017.
 */
interface RecyclerViewItemExpandCollapseListener<E> {
    fun onExpanded(model: E?, holder: RecyclerViewHolder<E>)
    fun onCollapsed(model: E?, holder: RecyclerViewHolder<E>)
}