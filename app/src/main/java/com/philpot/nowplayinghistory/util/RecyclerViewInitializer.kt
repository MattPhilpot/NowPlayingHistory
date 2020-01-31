package com.philpot.nowplayinghistory.util

import android.app.Activity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.widget.RecyclerSectionItemDecoration
import com.philpot.nowplayinghistory.widget.RecyclerViewPaginationListener

/**
 * Created by MattPhilpot on 10/30/2017.
 */
object RecyclerViewInitializer {
    fun initRecyclerView(activity: Activity?,
                         recyclerView: RecyclerView?,
                         adapter: RecyclerView.Adapter<*>?, addDivider: Boolean = true, paginationListener: RecyclerViewPaginationListener.RecyclerViewPaginationCallback? = null) {
        if (activity == null || recyclerView == null || adapter == null) {
            return
        }

        //recyclerView.addItemDecoration(RecyclerItemDividerDecoration(activity))
        if (addDivider) {
            recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        }
        if (adapter is RecyclerSectionItemDecoration.SectionCallback) {
            recyclerView.addItemDecoration(RecyclerSectionItemDecoration(activity.resources.getDimensionPixelSize(R.dimen.recycler_section_header_height), true, adapter))
        }

        val layoutManager = LinearLayoutManager(activity)
        paginationListener?.let {
            recyclerView.addOnScrollListener(RecyclerViewPaginationListener(layoutManager, it))
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }
}
