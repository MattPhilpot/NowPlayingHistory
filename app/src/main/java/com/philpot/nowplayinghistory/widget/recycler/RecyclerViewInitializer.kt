package com.philpot.nowplayinghistory.widget.recycler

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.philpot.nowplayinghistory.util.Utils

object RecyclerViewInitializer {

    private const val DEFAULT_COLUMN_COUNT = 1

    fun initRecyclerView(context: Context?,
                         recyclerView: RecyclerView?,
                         adapter: RecyclerView.Adapter<*>?,
                         orientation: Int = RecyclerView.VERTICAL,
                         divider: Boolean = false,
                         managerType: RecyclerViewManagerType = RecyclerViewManagerType.LINEAR,
                         maxColumns: Int = DEFAULT_COLUMN_COUNT
    ): Boolean {
        if (context == null || recyclerView == null || adapter == null) {
            return false
        }

        val numberOfColumns = try {
            Utils.getNumberOfColumns(context, maxColumns)
        } catch (e: Exception) {
            1
        }

        if (adapter is RecyclerSectionItemDecoration.SectionCallback) {
            adapter.showColumnCount = numberOfColumns
            recyclerView.addItemDecoration(
                RecyclerSectionItemDecoration(
                    adapter
                )
            )
        }

        if (divider) {
            recyclerView.addItemDecoration(
                RecyclerItemDividerDecoration(
                    context
                )
            )
        }

        val layoutManager: RecyclerView.LayoutManager = when(managerType) {
            RecyclerViewManagerType.LINEAR -> getLinearLayoutManager(
                context,
                orientation,
                false
            )
            //RecyclerViewManagerType.CENTER_SCROLL -> CenterLayoutManager(context, orientation, false)
            RecyclerViewManagerType.STAGGERED -> StaggeredGridLayoutManager(numberOfColumns, orientation)
            RecyclerViewManagerType.GRID -> GridLayoutManager(context, numberOfColumns, orientation, false)
        }

        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        return true
    }

    private fun getLinearLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean): LinearLayoutManager {
        return object : LinearLayoutManager(context, orientation, reverseLayout) {

            override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
                val linearSmoothScroller = object : LinearSmoothScroller(recyclerView?.context) {

                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
                    }
                }

                linearSmoothScroller.targetPosition = position
                startSmoothScroll(linearSmoothScroller)
            }
        }
    }

    private const val MILLISECONDS_PER_INCH = 5f
}