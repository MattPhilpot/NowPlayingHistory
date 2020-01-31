package com.philpot.nowplayinghistory.widget.favorite

import com.philpot.nowplayinghistory.widget.RecyclerViewHolder

/**
 * Created by MattPhilpot on 11/30/2017.
 */
interface RecyclerViewItemFavoriteClicked<E> {
    fun itemFavoriteClicked(model: E?, holder: RecyclerViewHolder<E>, isFavorite: Boolean)
}
