package com.philpot.nowplayinghistory.util

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.widget.Toast
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.MusicAppPreference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import com.philpot.nowplayinghistory.model.SongInfo


/**
 * Created by MattPhilpot on 11/2/2017.
 */
object Utils {

    fun startMusicAppIntent(context: Context, item: SongInfo, preference: MusicAppPreference) {
        if (preference == MusicAppPreference.None) {
            return
        }

        Utils.startMusicAppIntent(preference.packageName, getIntentFor(item), context)
    }

    fun getIntentFor(item: SongInfo): Intent {
        val intent = Intent()

        intent.action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
        intent.putExtra(SearchManager.QUERY, item.artist + " " + item.title)
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/audio")
        intent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, item.artist)
        intent.putExtra(MediaStore.EXTRA_MEDIA_TITLE, item.title)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        return intent
    }

    private fun startMusicAppIntent(packageName: String?, intent: Intent, context: Context) {
        packageName?.let {
            intent.`package` = it
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            packageName?.let {
                openAppInStore(context, it)
                return
            }
            Toast.makeText(context, R.string.error_opening_app, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openAppInStore(context: Context, packageName: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)))
        } catch (anfe: android.content.ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)))
        }
    }

    fun formatDateTime(timestamp: Long?): String {
        timestamp?.let {
            return SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(it))
        }
        return ""
    }

    fun getInternalCachePath(context: Context): String? {
        val f = File(context.cacheDir, "NowPlayingHistory")
        if (!f.exists()) {
            if (!f.mkdirs()) {
                // problem creating linux academy folder
                return null
            }
        }
        return f.absolutePath
    }

    fun dpToPx(dp: Float, resources: Resources): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
    }

    fun View.setVisibilityIfNeeded(newVisibility: Int) {
        if (visibility != newVisibility) {
            visibility = newVisibility
        }
    }

    fun getNumberOfColumns(context: Context, maxColumns: Int = 3): Int {
        val gridViewEntrySize = context.resources.getDimensionPixelSize(R.dimen.grip_view_entry_size)
        val gridViewSpacing = context.resources.getDimensionPixelSize(R.dimen.activity_content_margin)

        val displayMetrics = DisplayMetrics()

        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        var numColumns = (displayMetrics.widthPixels - gridViewSpacing) / (gridViewEntrySize + gridViewSpacing)

        if (numColumns > maxColumns) {
            numColumns = maxColumns
        } else if (numColumns < 1) {
            numColumns = 1
        }

        return numColumns
    }
}