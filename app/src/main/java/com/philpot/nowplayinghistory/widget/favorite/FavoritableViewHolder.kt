package com.philpot.nowplayinghistory.widget.favorite

import android.view.View
import com.philpot.nowplayinghistory.widget.RecyclerViewHolder

/**
 * Created by MattPhilpot on 11/30/2017.
 */
abstract class FavoritableViewHolder<E>(itemView: View) : RecyclerViewHolder<E>(itemView) {

    protected abstract val favoriteView: FavoriteButton?

    fun setItemFavoriteListener(favoriteClicked: RecyclerViewItemFavoriteClicked<E>) {
        favoriteView?.mOnFavoriteChangeListener = object : FavoriteButton.OnFavoriteChangeListener {
            override fun onFavoriteChanged(buttonView: FavoriteButton, favorite: Boolean) {
                favoriteClicked.itemFavoriteClicked(entity, this@FavoritableViewHolder, favorite)
            }
        }
    }
}