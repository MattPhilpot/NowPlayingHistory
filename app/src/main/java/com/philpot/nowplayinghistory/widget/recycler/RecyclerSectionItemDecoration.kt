package com.philpot.nowplayinghistory.widget.recycler

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.widget.CoreMapEntry


/**
 * Created by colse on 10/30/2017.
 */
class RecyclerSectionItemDecoration(private val sectionCallback: SectionCallback) : RecyclerView.ItemDecoration() {

    private val headerViewMap = LinkedHashMap<String, View>()
    private var currentHeaderView: View? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (sectionCallback.hasSections) {
            val position = parent.getChildAdapterPosition(view)
            var spanIndex = 0
            var spanSize = 1
            var spanCount = 1

            parent.layoutManager?.let { layoutManager ->
                when (layoutManager) {
                    is GridLayoutManager -> {
                        (view.layoutParams as GridLayoutManager.LayoutParams).let { lp ->
                            spanIndex = lp.spanIndex
                            spanSize = lp.spanSize
                        }
                        spanCount = layoutManager.spanCount
                    }
                    is StaggeredGridLayoutManager -> {
                        (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).let { lp ->
                            spanIndex = lp.spanIndex
                            spanSize = if (lp.isFullSpan) spanCount else 1
                        }
                        spanCount = layoutManager.spanCount
                    }
                    is LinearLayoutManager -> {
                        //do nothing
                    }
                }
            }

            if (isFirstRowOrColumn(position, spanIndex)) {
                outRect.top = getHeaderFor(parent, position).height
            }
        }
    }

    private fun isFirstRowOrColumn(position: Int, spanIndex: Int): Boolean {
        val prePosition = position - 1
        val preRowPosition = if (position > spanIndex) position - (1 + spanIndex) else -1
        val currentSectionHeader = sectionCallback.getSectionHeader(position)
        return prePosition == -1 || currentSectionHeader != sectionCallback.getSectionHeader(prePosition) ||
                preRowPosition == -1 || currentSectionHeader != sectionCallback.getSectionHeader(preRowPosition)
    }

    private fun getHeaderFor(parent: RecyclerView, position: Int): View {
        val title = sectionCallback.getSectionHeader(position).toString()
        return headerViewMap[title] ?: doBuildHeaderFor(parent, title, position)
    }

    private fun doBuildHeaderFor(parent: RecyclerView, title: String, position: Int): View {
        val headerView = (headerViewMap[title] ?: generateHeaderViewFor(parent, title)).apply {
            //findViewById<LinearLayout>(R.id.recycler_section_header_root)?.
            setBackgroundColor(sectionCallback.getSectionColor(position))
            /*
            findViewById<ImageView>(R.id.list_item_section_image)?.apply {
                val imageResId = sectionCallback.getSectionDrawable(position)
                visibility = if (imageResId > 0) {
                    setImageResource(imageResId)
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
            */
        }
        headerView.findViewById<TextView>(R.id.list_item_section_text).apply {
            text = title
            setTextColor(sectionCallback.getSectionTextColor(position))
        }
        fixLayoutSize(headerView, parent)
        return headerView
    }

    private fun generateHeaderViewFor(parent: RecyclerView, title: String): View {
        val retVal = inflateHeaderView(parent)
        headerViewMap[title] = retVal
        return retVal
    }

    private fun inflateHeaderView(parent: RecyclerView): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.recycler_section_header, parent, false)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        if (sectionCallback.hasSections) {
            var previousHeader: CharSequence = ""

            val titleIndexes = mutableListOf<Map.Entry<View, Int>>()

            (0 until parent.childCount).forEach { i ->
                val child = parent.getChildAt(i)
                val position = parent.getChildAdapterPosition(child)
                val title = sectionCallback.getSectionHeader(position).toString()

                if (previousHeader != title) {
                    titleIndexes.add(
                        CoreMapEntry(
                            child,
                            position
                        )
                    )
                    previousHeader = title
                }
            }

            var alreadyTranslated = false
            titleIndexes.withIndex().reversed().forEach { i ->
                val headerView = getHeaderFor(parent, i.value.value)
                var previousView: View? = null
                titleIndexes.getOrNull(i.index - 1)?.let { previousEntry ->
                    previousView = getHeaderFor(parent, previousEntry.value)
                }

                alreadyTranslated = drawHeader2(c, i.value.key, headerView, previousView, alreadyTranslated)
            }
        }
    }

    private fun drawHeader2(c: Canvas, child: View, headerView: View, previousView: View?, wasAlreadyTranslated: Boolean): Boolean {
        if (previousView == null || currentHeaderView == null) {
            currentHeaderView = headerView
        }
        var translatedPrevious = false

        val topOfHeader = (child.top - headerView.height).toFloat()

        //val previousHeader = headerViewMap.values.elementAtOrNull(positionInMap - 1)
        if (previousView != null &&
            topOfHeader < previousView.bottom.toFloat()) {
            c.save()
            c.translate(0F, topOfHeader - previousView.height)
            previousView.draw(c)
            c.restore()
            currentHeaderView = headerView
            translatedPrevious = true
        }

        if (!wasAlreadyTranslated) {
            c.save()
            if (headerView === currentHeaderView || Math.abs(child.top) < Math.abs(child.height - headerView.height)) {
                c.translate(0F, Math.max(0F, topOfHeader))
            } else {
                c.translate(0F, topOfHeader)
            }
            headerView.draw(c)
            c.restore()
        }
        return translatedPrevious
    }

    /**
     * Measures the header view to make sure its size is greater than 0 and will be drawn
     */
    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidth = ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeight = ViewGroup.getChildMeasureSpec(heightSpec, parent.paddingTop + parent.paddingBottom, view.layoutParams.height)

        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    interface SectionCallback {
        val hasSections: Boolean
        var showColumnCount: Int
        fun isSection(position: Int): Boolean
        fun getSectionHeader(position: Int): CharSequence
        fun getSectionColor(position: Int): Int
        fun getSectionTextColor(position: Int): Int
        fun getSectionDrawable(position: Int): Int
    }
}
