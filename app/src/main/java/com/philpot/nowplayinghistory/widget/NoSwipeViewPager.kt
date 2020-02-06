package com.philpot.nowplayinghistory.widget

import android.content.Context
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import java.lang.reflect.AccessibleObject.setAccessible
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent



/**
 * Created by MattPhilpot on 12/11/2017.
 */
class NoSwipeViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    var swipeEnabled: Boolean = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.swipeEnabled) {
            super.onTouchEvent(event)
        } else {
            false
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.swipeEnabled) {
            super.onInterceptTouchEvent(event)
        } else {
            false
        }

    }
}