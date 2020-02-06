package com.philpot.nowplayinghistory.util

import android.content.Context
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.db2.dao.HistoryDao
import com.philpot.nowplayinghistory.db2.dao.SongInfoDao
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.MusicAppPreference

/**
 * Created by MattPhilpot on 11/2/2017.
 */
object ShortcutHelper {

    private fun getMusicAppPreference(context: Context): MusicAppPreference {
        //val preferences = context.getSharedPreferences(context.getString(R.string.app_preferences_file), Context.MODE_PRIVATE)
        //return MusicAppPreference.getFromOrdinal(preferences.getInt(Preferences.CurrentMusicApp.value, 0))
        return MusicAppPreference.GoogleMusic
    }

    fun updateShortcuts(context: Context, historyDao: HistoryDao, songInfoDao: SongInfoDao) { //}, albumArtProvider: AlbumArtCacheProvider?) {
        val musicAppPreference = getMusicAppPreference(context)
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)
        val shortCutList = mutableListOf<ShortcutInfo>()

        if (musicAppPreference == MusicAppPreference.None) {
            shortcutManager.dynamicShortcuts = shortCutList
        }

        for(each in historyDao.getMostRecentItems(3).reversed()) {
            val song = songInfoDao.getById(each.id)!!
            val intent = Utils.getIntentFor(song)
            musicAppPreference.packageName?.let {
                intent.`package` = it
            }

            val shortCut = ShortcutInfo.Builder(context, song.title)
                    .setShortLabel(song.title)
                    .setLongLabel(song.title + " " + song.artist)
                    .setIcon(getShortCutIcon(context, each))
                    .setIntent(intent)
                    .build()
            shortCutList.add(shortCut)
        }

        shortcutManager.dynamicShortcuts = shortCutList
    }

    private fun getShortCutIcon(context: Context, entry: HistoryEntry): Icon {
        /*
        try {
            albumArtProvider?.attemptCacheFetch(entry)?.let {
                return Icon.createWithBitmap(it)
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(ShortcutHelper::class.java.simpleName, e.message, e)
            }
        }
        */

        return Icon.createWithResource(context, R.drawable.ic_music_note_black)
    }
}