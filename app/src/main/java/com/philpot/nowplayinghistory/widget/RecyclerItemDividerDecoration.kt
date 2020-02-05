package com.philpot.nowplayinghistory.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.philpot.nowplayinghistory.R

/**
 * Created by colse on 10/31/2017.
 */
class RecyclerItemDividerDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private var mDivider: Drawable? = ContextCompat.getDrawable(context, R.drawable.line_divider)
    private val mBounds = Rect()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        if (parent.layoutManager == null || mDivider == null) {
            return
        }
        drawVertical(c, parent)
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        mDivider?.let {
            canvas.save()
            val left: Int = parent.context.resources.getDimension(R.dimen.default_padding).toInt()
            val right: Int = parent.width - left

            canvas.clipRect(left, parent.paddingTop, right,
                    parent.height - parent.paddingBottom)

            val childCount = parent.childCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                parent.getDecoratedBoundsWithMargins(child, mBounds)
                val bottom = mBounds.bottom + Math.round(child.translationY)
                val top = bottom - it.intrinsicHeight
                it.setBounds(left, top, right, bottom)
                it.draw(canvas)
            }
            canvas.restore()
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        mDivider?.let {
            outRect.set(0, 0, 0, it.intrinsicHeight)
            return
        }
        outRect.set(0, 0, 0, 0)
    }
}