package com.philpot.nowplayinghistory.widget

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by MattPhilpot on 11/7/2017.
 */
class RecyclerViewPaginationListener(private val layoutManager: LinearLayoutManager,
                                     private val callback: RecyclerViewPaginationCallback) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        determineIfLoadMore()
    }

    private fun determineIfLoadMore() {
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!callback.isLoading() && !callback.isLastPage()) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                callback.loadMoreItems()
            }
        }
    }

    interface RecyclerViewPaginationCallback {
        fun isLoading(): Boolean
        fun isLastPage(): Boolean
        fun loadMoreItems()
    }
}