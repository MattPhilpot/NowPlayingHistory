package com.philpot.nowplayinghistory.info.bottomsheet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.LinearLayout
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.util.Utils
import kotlinx.android.synthetic.main.bottom_sheet_song_history_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by MattPhilpot on 11/13/2017.
 */
class SongInfoHistoryItemView(context: Context) : LinearLayout(context, null, 0) {

    init {
        inflate(context, R.layout.bottom_sheet_song_history_item, this)
    }

    fun updateWith(entry: HistoryEntry) {
        val timestamp = context.getString(R.string.history_info_heard, Utils.formatDateTime(entry.timestamp.millis))
        bottom_sheet_song_history_item_time?.text = getDateAsString(entry.timestamp.millis)
        bottom_sheet_song_history_item_time_stamp?.text = timestamp

        entry.location?.let { location ->
            bottom_sheet_song_history_item_location?.setImageResource(R.drawable.ic_location_on_black)
            bottom_sheet_song_history_item_location?.setOnClickListener {
                //val songByString = context.getString(R.string.google_maps_song_by_artist, entry.title, entry.artist)
                val uri = Uri.parse("geo:0,0?q=${location.latitude},${location.longitude}(testing)")
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                mapIntent.`package` = "com.google.android.apps.maps"
                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)
                }
            }
        }
    }

    private fun getDateAsString(timestamp: Long): String {
        return SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(Date(timestamp)).toString()
    }
}