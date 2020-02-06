package com.philpot.nowplayinghistory.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.db2.NowPlayingDatabase
import com.philpot.nowplayinghistory.db2.dao.HistoryDao
import com.philpot.nowplayinghistory.db2.dao.SongInfoDao
import com.philpot.nowplayinghistory.model.HistoryEntry
import com.philpot.nowplayinghistory.util.Utils
import org.kodein.di.android.closestKodein

/**
 * Created by MattPhilpot on 11/4/2017.
 */
class NowPlayingRemoteViewsFactory(private val context: Context, private val intent: Intent) : RemoteViewsService.RemoteViewsFactory {

    private val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

    private val historyDao: HistoryDao = NowPlayingDatabase.getInstance(context).historyDao()
    private val songDao: SongInfoDao = NowPlayingDatabase.getInstance(context).songDao()

    private var historyList: List<HistoryEntry> = arrayListOf()

    override fun onCreate() {
        historyList = historyDao.getMostRecentItems(100)
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        historyList = historyDao.getMostRecentItems(100)
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        val retVal = RemoteViews(context.packageName, R.layout.appwidget_listview_item)
        val historyItem = historyList[position]

        val item = songDao.getById(historyItem.songId)
        retVal.setTextViewText(R.id.appwidget_listview_item_title, item?.title ?: "")
        retVal.setTextViewText(R.id.appwidget_listview_item_artist, item?.artist ?: "")
        retVal.setTextViewText(R.id.appwidget_listview_item_date, Utils.formatDateTime(historyItem.timestamp.millis))


        retVal.setPendingIntentTemplate(R.id.appwidget_listview_item_root, PendingIntent.getActivity(context, 0, Utils.getIntentFor(item!!), PendingIntent.FLAG_UPDATE_CURRENT))

        return retVal
    }

    override fun getCount(): Int {
        return historyList.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {

    }
}