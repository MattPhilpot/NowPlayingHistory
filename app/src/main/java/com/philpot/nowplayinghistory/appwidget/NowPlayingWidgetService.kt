package com.philpot.nowplayinghistory.appwidget

import android.content.Intent
import android.widget.RemoteViewsService

/**
 * Created by MattPhilpot on 11/4/2017.
 */
class NowPlayingWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return NowPlayingRemoteViewsFactory(applicationContext, intent)
    }
}