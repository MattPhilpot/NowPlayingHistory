package com.philpot.nowplayinghistory.util

import android.util.Log
import com.philpot.nowplayinghistory.BuildConfig

object ExceptionUtil {

    fun logWithAnalytics(tag: String, t: Throwable) {
        val message = Log.getStackTraceString(t)
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, t)
        } /*else if (Fabric.isInitialized()) {
            Crashlytics.log(message)
            Crashlytics.logException(t)
        }*/
    }
}