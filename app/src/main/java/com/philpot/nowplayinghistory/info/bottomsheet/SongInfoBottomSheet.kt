package com.philpot.nowplayinghistory.info.bottomsheet

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.db2.dao.AlbumInfoDao
import com.philpot.nowplayinghistory.db2.dao.HistoryDao
import com.philpot.nowplayinghistory.db2.dao.SongInfoDao
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.model.HistoryItem
import kotlinx.android.synthetic.main.bottom_sheet_song_info.view.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

/**
 * Created by MattPhilpot on 11/12/2017.
 */
class SongInfoBottomSheet(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs, 0), KodeinAware {

    private val parentKodein by closestKodein()

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
    }

    private val historyDao by instance<HistoryDao>()
    //private val songInfoInfoDao by instance<SongInfoDao>()
    //private val albumInfoInfoDao by instance<AlbumInfoDao>()

    var currentHistoryEntry: HistoryItem? = null
        private set

    init {
        View.inflate(context, R.layout.bottom_sheet_song_info, this)
        this.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDisabledLight))
    }

    //fun getPeekHeight(): Int? = bottom_sheet_song_info_root?.measuredHeight

    fun updateWith(historyEntry: HistoryItem) {
        currentHistoryEntry = historyEntry
        bottom_sheet_song_info_title?.text = historyEntry.songInfo.title
        bottom_sheet_song_info_artist?.text = historyEntry.songInfo.artist

        bottom_sheet_song_info_album?.text = historyEntry.albumInfo?.title ?: ""

        addHistoryChildren(historyEntry)

        /*
        historyEntry.songInfo?.albumInfo?.albumBitmap?.let {
            bottom_sheet_song_info_album_art?.setImageBitmap(it)
            return
        }
        */

        bottom_sheet_song_info_album_art?.setImageResource(R.drawable.ic_music_note_white)
    }

    private fun addHistoryChildren(historyEntry: HistoryItem) {
        /*
        bottom_sheet_song_info_history_root?.let { view ->
            view.removeAllViews()
            historyDao?.let { dao ->
                for (each in dao.getMostRecentItems(8, historyEntry)) {
                    view.addView(getHistoryView(each))
                }
            }
        }
        */
    }

    private fun getHistoryView(entry: HistoryEntry): View {
        val retVal = SongInfoHistoryItemView(context)
        retVal.updateWith(entry)
        return retVal
    }

}