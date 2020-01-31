package com.philpot.nowplayinghistory.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.db.dao.HistoryDao
import com.philpot.nowplayinghistory.model.HistoryItem
import com.philpot.nowplayinghistory.util.Utils

/**
 * Created by MattPhilpot on 11/4/2017.
 */
class NowPlayingRemoteViewsFactory(private val context: Context, private val intent: Intent) : RemoteViewsService.RemoteViewsFactory {

    private val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

    private val historyDao: HistoryDao = context.appKodein.invoke().instance()

    private var historyList: List<HistoryItem> = arrayListOf()

    override fun onCreate() {
        historyList = historyDao.getAll()
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        historyList = historyDao.getAll()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        val retVal = RemoteViews(context.packageName, R.layout.appwidget_listview_item)
        val historyItem = historyList[position]

        retVal.setTextViewText(R.id.appwidget_listview_item_title, historyItem.title)
        retVal.setTextViewText(R.id.appwidget_listview_item_artist, historyItem.artist)
        retVal.setTextViewText(R.id.appwidget_listview_item_date, Utils.formatDateTime(historyItem.timestamp))

        retVal.setPendingIntentTemplate(R.id.appwidget_listview_item_root, PendingIntent.getActivity(context, 0, Utils.getIntentFor(historyItem), PendingIntent.FLAG_UPDATE_CURRENT))

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