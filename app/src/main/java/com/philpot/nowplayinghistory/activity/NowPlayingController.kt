package com.philpot.nowplayinghistory.activity

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import com.philpot.nowplayinghistory.listener.NowPlayingListener

/**
 * Created by MattPhilpot on 10/29/2017.
 */
class NowPlayingController(private var view: NowPlayingView?) {

    fun onResume(context: Context) {
        if (isNotifyServiceEnabled(context)) {
            view?.hideNotificationAccessDialog()
        } else {
            view?.showNotificationAccessDialog()
        }
    }

    fun destroy() {
        view = null
    }

    private fun isNotifyServiceEnabled(context: Context): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationListenerAccessGranted(ComponentName(context.packageName, NowPlayingListener::class.java.name))
    }

    interface NowPlayingView {
        fun showNotificationAccessDialog()
        fun hideNotificationAccessDialog()
    }
}
