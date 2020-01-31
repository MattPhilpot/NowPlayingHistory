package com.philpot.nowplayinghistory.activity

import android.app.NotificationManager
import com.philpot.nowplayinghistory.db.dao.HistoryDao
import com.philpot.nowplayinghistory.model.HistoryItem
import android.text.TextUtils
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.philpot.nowplayinghistory.db.dao.SongInfoDao
import com.philpot.nowplayinghistory.db.manager.SongAlbumManager
import com.philpot.nowplayinghistory.event.EventBus
import com.philpot.nowplayinghistory.event.NewHistoryItemEvent
import com.philpot.nowplayinghistory.listener.NowPlayingListener
import com.philpot.nowplayinghistory.util.TestRecordInsertUtil
import com.philpot.nowplayinghistory.widget.RecyclerViewPaginationListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.isNotificationListenerAccessGranted(ComponentName(context.packageName, NowPlayingListener::class.java.name))
        } else {
            pre27IsNotifyServiceEnabled(context)
        }
    }

    private fun pre27IsNotifyServiceEnabled(context: Context): Boolean {
        val cn = ComponentName(context, NowPlayingListener::class.java)
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains(cn.flattenToString())
    }

    interface NowPlayingView {
        fun showNotificationAccessDialog()
        fun hideNotificationAccessDialog()
    }
}