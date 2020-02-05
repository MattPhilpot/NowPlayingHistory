package com.philpot.nowplayinghistory.listener

import android.Manifest
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
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
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.Preferences
import com.philpot.nowplayinghistory.model.Song
import com.philpot.nowplayinghistory.util.ShortcutHelper
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.philpot.nowplayinghistory.BuildConfig
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.model.HistoryEntryLocation
import org.joda.time.DateTime
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein


/**
 * Created by colse on 10/29/2017.
 */
class NowPlayingListener : NotificationListenerService(), KodeinAware {

    private val parentKodein by closestKodein(applicationContext)

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
    }


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

        val toAdd = HistoryEntry(title = title, artist = artist, timestamp = DateTime.now())
        if (historyDao.insertIfNotRepeat(toAdd)) {
            doHistoryItemUpdate(toAdd, historyDao, kodein.instance(), kodein.instance())
        }
    }

    private fun doHistoryItemUpdate(entry: HistoryEntry, historyDao: HistoryDao, songInfoDao: SongInfoDao, albumArtProvider: AlbumArtCacheProvider) {
        try {
            songInfoDao.updateLastHeard(entry)
            val preferences = applicationContext?.getSharedPreferences(getString(R.string.app_preferences_file), Context.MODE_PRIVATE) as SharedPreferences
            doGPSIfAble(entry, historyDao, preferences)
            getAlbumArtForShortcuts(preferences, albumArtProvider, entry, historyDao)
        } catch (e : Exception) {
            // do nothing, just don't crash
        } finally {
            EventBus.getDefault().post(NewHistoryItemEvent(entry))
        }
    }

    private fun doGPSIfAble(entry: HistoryEntry, historyDao: HistoryDao, preferences: SharedPreferences) {
        if (preferences.getBoolean(Preferences.GPSEnable.value, false) &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                if (it.hasAccuracy() && entry.timestamp - it.time < 300000) {
                    saveHistoryItemWithLocation(it, entry, historyDao)
                    return
                }
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0F, object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    location?.let {
                        if (it.hasAccuracy()) {
                            saveHistoryItemWithLocation(it, entry, historyDao)
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

    private fun saveHistoryItemWithLocation(location: Location, entry: HistoryEntry, historyDao: HistoryDao) {
        entry.location = HistoryEntryLocation(longitude = location.longitude, latitude = location.latitude, accuracy = location.accuracy)
        historyDao.insertOrUpdate(entry)
    }

    private fun getAlbumArtForShortcuts(preferences: SharedPreferences, albumArtProvider: AlbumArtCacheProvider, entry: HistoryEntry, historyDao: HistoryDao) {
        try {
            if (preferences.getBoolean(Preferences.LastFmIntegration.value, false)) {
                albumArtProvider.getAlbumInfoAsync(entry, object : AlbumArtCacheProvider.AlbumArtCallback {
                    override fun onAlbumArtLoaded(bitmap: Bitmap?, song: Song) {
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