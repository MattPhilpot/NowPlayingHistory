package com.philpot.nowplayinghistory.listener

import android.Manifest
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import android.content.Intent
import android.os.IBinder
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.util.Log
import com.philpot.nowplayinghistory.db.dao.HistoryDao
import com.philpot.nowplayinghistory.db.dao.SongInfoDao
import com.philpot.nowplayinghistory.event.NewHistoryItemEvent
import com.philpot.nowplayinghistory.info.AlbumArtCacheProvider
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.model.Preferences
import com.philpot.nowplayinghistory.model.SongInfo
import com.philpot.nowplayinghistory.util.ShortcutHelper
import org.greenrobot.eventbus.EventBus
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.philpot.nowplayinghistory.BuildConfig
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.model.HistoryItemLocation


/**
 * Created by colse on 10/29/2017.
 */
class NowPlayingListener : NotificationListenerService() {

    override fun onBind(mIntent: Intent): IBinder? {
        val mIBinder = super.onBind(mIntent)
        logInfo("onBind")
        return mIBinder
    }

    override fun onUnbind(mIntent: Intent): Boolean {
        val mOnUnbind = super.onUnbind(mIntent)
        logInfo("onUnbind")
        return mOnUnbind
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        logInfo("Listener Connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        logInfo("Listener Disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            if ("com.google.intelligence.sense" == it.packageName) {
                try {
                    val entry = it.notification.extras["android.title"].toString()
                    logInfo(entry)
                    saveNewEntry(entry)
                } catch (e: Exception) {
                    logError("Error saving entry", e)
                }
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        /*
        if (sbn.packageName == "com.google.intelligence.sense") {
            logInfo("Now Playing Removed")
        }
        */
    }

    private fun saveNewEntry(entry: String) {
        val split = entry.split(" by ")

        if (split.size < 2) {
            return
        }

        var title = ""
        val artist = split.last()

        for (i in 0..split.size - 2) {
            if (title.isNotBlank()) {
                title += " by "
            }
            title += split[i]
        }

        if (title.isBlank() || artist.isBlank()) {
            return
        }

        val kodein = appKodein.invoke()
        val historyDao = kodein.instance<HistoryDao>()
        val toAdd = HistoryItem(title = title, artist = artist, timestamp = System.currentTimeMillis())
        if (historyDao.insertIfNotRepeat(toAdd)) {
            doHistoryItemUpdate(toAdd, historyDao, kodein.instance(), kodein.instance())
        }
    }

    private fun doHistoryItemUpdate(item: HistoryItem, historyDao: HistoryDao, songInfoDao: SongInfoDao, albumArtProvider: AlbumArtCacheProvider) {
        try {
            songInfoDao.updateLastHeard(item)
            val preferences = applicationContext?.getSharedPreferences(getString(R.string.app_preferences_file), Context.MODE_PRIVATE) as SharedPreferences
            doGPSIfAble(item, historyDao, preferences)
            getAlbumArtForShortcuts(preferences, albumArtProvider, item, historyDao)
        } catch (e : Exception) {
            // do nothing, just don't crash
        } finally {
            EventBus.getDefault().post(NewHistoryItemEvent(item))
        }
    }

    private fun doGPSIfAble(item: HistoryItem, historyDao: HistoryDao, preferences: SharedPreferences) {
        if (preferences.getBoolean(Preferences.GPSEnable.value, false) &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                if (it.hasAccuracy() && item.timestamp - it.time < 300000) {
                    saveHistoryItemWithLocation(it, item, historyDao)
                    return
                }
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0F, object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    location?.let {
                        if (it.hasAccuracy()) {
                            saveHistoryItemWithLocation(it, item, historyDao)
                            locationManager.removeUpdates(this)
                            return
                        }
                    }
                }
                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) { /* do nothing */ }
                override fun onProviderEnabled(p0: String?) { /* do nothing */ }
                override fun onProviderDisabled(p0: String?) { /* do nothing */ }
            })
        }
    }

    private fun saveHistoryItemWithLocation(location: Location, item: HistoryItem, historyDao: HistoryDao) {
        item.location = HistoryItemLocation(longitude = location.longitude, latitude = location.latitude, accuracy = location.accuracy)
        historyDao.insertOrUpdate(item)
    }

    private fun getAlbumArtForShortcuts(preferences: SharedPreferences, albumArtProvider: AlbumArtCacheProvider, item: HistoryItem, historyDao: HistoryDao) {
        try {
            if (preferences.getBoolean(Preferences.LastFmIntegration.value, false)) {
                albumArtProvider.getAlbumInfoAsync(item, object : AlbumArtCacheProvider.AlbumArtCallback {
                    override fun onAlbumArtLoaded(bitmap: Bitmap?, songInfo: SongInfo) {
                        ShortcutHelper.updateShortcuts(applicationContext, historyDao, albumArtProvider)
                    }
                })
            }
        } catch (e : Exception) {
            ShortcutHelper.updateShortcuts(applicationContext, historyDao, albumArtProvider)
        }
    }

    private fun logError(message: String, error: Exception) {
        Log.e(TAG, message, error)
    }

    private fun logInfo(message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, message)
        }
    }
}