package com.philpot.nowplayinghistory.info.bottomsheet

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.db.dao.AlbumInfoDao
import com.philpot.nowplayinghistory.db.dao.HistoryDao
import com.philpot.nowplayinghistory.db.dao.SongInfoDao
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.widget.favorite.FavoriteButton
import kotlinx.android.synthetic.main.bottom_sheet_song_info.view.*

/**
 * Created by MattPhilpot on 11/12/2017.
 */
class SongInfoBottomSheet(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs, 0) {

    private var historyDao: HistoryDao? = null
    private var songInfoDao: SongInfoDao? = null
    private var albumInfoDao: AlbumInfoDao? = null

    var currentHistoryItem: HistoryItem? = null
        private set

    init {
        View.inflate(context, R.layout.bottom_sheet_song_info, this)
    }

    fun initialize(historyDao: HistoryDao,
                   songInfoDao: SongInfoDao,
                   albumInfoDao: AlbumInfoDao) {
        this.historyDao = historyDao
        this.songInfoDao = songInfoDao
        this.albumInfoDao = albumInfoDao
    }

    //fun getPeekHeight(): Int? = bottom_sheet_song_info_root?.measuredHeight

    fun updateWith(historyItem: HistoryItem) {
        currentHistoryItem = historyItem
        bottom_sheet_song_info_title?.text = historyItem.title
        bottom_sheet_song_info_artist?.text = historyItem.artist

        bottom_sheet_song_info_album?.text = historyItem.songInfo?.album ?:
                historyItem.songInfo?.albumInfo?.title ?: ""

        addHistoryChildren(historyItem)

        historyItem.songInfo?.albumInfo?.albumBitmap?.let {
            bottom_sheet_song_info_album_art?.setImageBitmap(it)
            return
        }
        bottom_sheet_song_info_album_art?.setImageResource(R.drawable.ic_music_note_white)
    }

    private fun addHistoryChildren(historyItem: HistoryItem) {
        bottom_sheet_song_info_history_root?.let { view ->
            view.removeAllViews()
            historyDao?.let { dao ->
                for (each in dao.getLastNSongs(8, historyItem)) {
                    view.addView(getHistoryView(each))
                }
            }
        }
    }

    private fun getHistoryView(item: HistoryItem): View {
        val retVal = SongInfoHistoryItemView(context)
        retVal.updateWith(item)
        return retVal
    }

}