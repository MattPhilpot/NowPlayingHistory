package com.philpot.nowplayinghistory.widget

/**
 * Created by MattPhilpot on 11/6/2017.
 */
interface RecyclerViewItemDeleteToggled<in E> {
    fun deleteToggled(model: E?, holder: DeletableViewHolder)
}