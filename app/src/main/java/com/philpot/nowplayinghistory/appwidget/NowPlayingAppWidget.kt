package com.philpot.nowplayinghistory.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.widget.RemoteViews

import com.philpot.nowplayinghistory.R

/**
 * Implementation of App Widget functionality.
 */
class NowPlayingAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        appWidgetIds.forEach {
            updateAppWidget(context, appWidgetManager, it)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        intent?.let {
            if (it.action == MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH) {
                context?.startActivity(intent)
            }
        }
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val intent = Intent(context, NowPlayingWidgetService::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val views = RemoteViews(context.packageName, R.layout.now_playing_app_widget)
            views.setRemoteAdapter(R.id.appwidget_listview, intent)

            val clickIntent = Intent()
            intent.action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

            views.setPendingIntentTemplate(R.id.appwidget_listview, PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT))

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

    }
}

