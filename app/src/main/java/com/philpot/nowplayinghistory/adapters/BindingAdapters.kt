package com.philpot.nowplayinghistory.adapters

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.api.load
import com.philpot.nowplayinghistory.widget.favorite.FavoriteButton
import com.philpot.nowplayinghistory.widget.flip.FlipImageView


@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("isRefreshing")
fun bindIsRefreshing(view: SwipeRefreshLayout, loading: Boolean) {
    view.isRefreshing = loading
}

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        view.load(imageUrl) {
            crossfade(true)

        }
    }
}

@BindingAdapter("frontImageFromUrl")
fun bindImageFromUrl(view: FlipImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        view.frontImageView?.load(imageUrl) {
            crossfade(true)
        }
    }
}

@BindingAdapter("isFavorite")
fun bindIsFavorite(view: FavoriteButton, boolean: Boolean?) {
    view.isFavorite = boolean ?: false
}
