package com.philpot.nowplayinghistory.info.bottomsheet

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.db2.dao.AlbumDao
import com.philpot.nowplayinghistory.db2.dao.HistoryDao
import com.philpot.nowplayinghistory.db2.dao.SongDao
import com.philpot.nowplayinghistory.model.HistoryEntry
import kotlinx.android.synthetic.main.bottom_sheet_song_info.view.*

/**
 * Created by MattPhilpot on 11/12/2017.
 */
class SongInfoBottomSheet(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs, 0) {

    private var historyDao: HistoryDao? = null
    private var songInfoDao: SongDao? = null
    private var albumInfoDao: AlbumDao? = null

    var currentHistoryEntry: HistoryEntry? = null
        private set

    init {
        View.inflate(context, R.layout.bottom_sheet_song_info, this)
    }

    fun initialize(historyDao: HistoryDao,
                   songInfoDao: SongDao,
                   albumInfoDao: AlbumDao) {
        this.historyDao = historyDao
        this.songInfoDao = songInfoDao
        this.albumInfoDao = albumInfoDao
    }

    //fun getPeekHeight(): Int? = bottom_sheet_song_info_root?.measuredHeight

    fun updateWith(historyEntry: HistoryEntry) {
        currentHistoryEntry = historyEntry
        bottom_sheet_song_info_title?.text = historyEntry.title
        bottom_sheet_song_info_artist?.text = historyEntry.artist

        bottom_sheet_song_info_album?.text = historyEntry.songInfo?.album ?:
                historyEntry.songInfo?.albumInfo?.title ?: ""

        addHistoryChildren(historyEntry)

        historyEntry.songInfo?.albumInfo?.albumBitmap?.let {
            bottom_sheet_song_info_album_art?.setImageBitmap(it)
            return
        }
        bottom_sheet_song_info_album_art?.setImageResource(R.drawable.ic_music_note_white)
    }

    private fun addHistoryChildren(historyEntry: HistoryEntry) {
        bottom_sheet_song_info_history_root?.let { view ->
            view.removeAllViews()
            historyDao?.let { dao ->
                for (each in dao.getLastNSongs(8, historyEntry)) {
                    view.addView(getHistoryView(each))
                }
            }
        }
    }

    private fun getHistoryView(entry: HistoryEntry): View {
        val retVal = SongInfoHistoryItemView(context)
        retVal.updateWith(entry)
        return retVal
    }

}