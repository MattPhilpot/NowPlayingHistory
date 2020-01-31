package com.philpot.nowplayinghistory.util

import android.content.Context
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.util.Log
import com.philpot.nowplayinghistory.BuildConfig
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.db.dao.HistoryDao
import com.philpot.nowplayinghistory.info.AlbumArtCacheProvider
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.model.MusicAppPreference
import com.philpot.nowplayinghistory.model.Preferences

/**
 * Created by MattPhilpot on 11/2/2017.
 */
object ShortcutHelper {

    private fun getMusicAppPreference(context: Context): MusicAppPreference {
        val preferences = context.getSharedPreferences(context.getString(R.string.app_preferences_file), Context.MODE_PRIVATE)
        return MusicAppPreference.getFromOrdinal(preferences.getInt(Preferences.CurrentMusicApp.value, 0))
    }

    fun updateShortcuts(context: Context, historyDao: HistoryDao, albumArtProvider: AlbumArtCacheProvider?) {
        val musicAppPreference = getMusicAppPreference(context)
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)
        val shortCutList = mutableListOf<ShortcutInfo>()

        if (musicAppPreference == MusicAppPreference.None) {
            shortcutManager.dynamicShortcuts = shortCutList
        }

        for(each in historyDao.getLastNSongs(3).reversed()) {
            val intent = Utils.getIntentFor(each)
            musicAppPreference.packageName?.let {
                intent.`package` = it
            }

            val shortCut = ShortcutInfo.Builder(context, each.title)
                    .setShortLabel(each.title)
                    .setLongLabel(each.title + " " + each.artist)
                    .setIcon(getShortCutIcon(context, each, albumArtProvider))
                    .setIntent(intent)
                    .build()
            shortCutList.add(shortCut)
        }

        shortcutManager.dynamicShortcuts = shortCutList
    }

    private fun getShortCutIcon(context: Context, item: HistoryItem, albumArtProvider: AlbumArtCacheProvider?): Icon {
        try {
            albumArtProvider?.attemptCacheFetch(item)?.let {
                return Icon.createWithBitmap(it)
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(ShortcutHelper::class.java.simpleName, e.message, e)
            }
        }

        return Icon.createWithResource(context, R.drawable.ic_music_note_black)
    }
}