package com.philpot.nowplayinghistory

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.philpot.nowplayinghistory.db.StorIOSqliteOpenHelper
import com.philpot.nowplayinghistory.db.dao.AlbumInfoDao
import com.philpot.nowplayinghistory.db.dao.DaoCache
import com.philpot.nowplayinghistory.db.dao.HistoryDao
import com.philpot.nowplayinghistory.db.dao.SongInfoDao
import com.philpot.nowplayinghistory.db.manager.SongAlbumManager
import com.philpot.nowplayinghistory.event.EventBus
import com.philpot.nowplayinghistory.event.NPEventBus
import com.philpot.nowplayinghistory.info.AlbumArtCacheProvider
import com.philpot.nowplayinghistory.info.NowPlayingArtProvider
import com.philpot.nowplayinghistory.lastfm.Lfm
import com.philpot.nowplayinghistory.model.AlbumInfo
import com.philpot.nowplayinghistory.model.SongInfo
import com.philpot.nowplayinghistory.model.HistoryItem
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.squareup.leakcanary.LeakCanary
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import org.kodein.di.erased.singleton

/**
 * Created by colse on 10/29/2017.
 */
class NowPlayingHistoryApplication : Application(), KodeinAware {

    override val kodein by Kodein.lazy {

        bind<StorIOSQLite.LowLevel>() with singleton {
            instance<StorIOSqliteOpenHelper>().getStorIO().lowLevel()
        }

        bind<DaoCache>() with provider {
            instance<StorIOSqliteOpenHelper>()
        }

        bind<StorIOSqliteOpenHelper>() with singleton {
            val retVal = StorIOSqliteOpenHelper(this@NowPlayingHistoryApplication)
            retVal.writableDatabase
            retVal
        }

        bind<HistoryDao>() with provider {
            instance<StorIOSqliteOpenHelper>().getDaoFor<HistoryItem, HistoryDao>(HistoryItem::class.java)
        }

        bind<SongInfoDao>() with provider {
            instance<StorIOSqliteOpenHelper>().getDaoFor<SongInfo, SongInfoDao>(SongInfo::class.java)
        }

        bind<AlbumInfoDao>() with provider {
            instance<StorIOSqliteOpenHelper>().getDaoFor<AlbumInfo, AlbumInfoDao>(AlbumInfo::class.java)
        }

        bind<EventBus>() with singleton {
            NPEventBus(org.greenrobot.eventbus.EventBus.getDefault())
        }

        bind<AlbumArtCacheProvider>() with singleton {
            NowPlayingArtProvider(instance(), instance(), applicationContext.cacheDir.absolutePath)
        }

        bind<SongAlbumManager>() with singleton {
            SongAlbumManager(instance(), instance(), instance())
        }

        bind<SharedPreferences>() with singleton {
            instance<Context>().getSharedPreferences(getString(R.string.app_preferences_file), Context.MODE_PRIVATE)
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        Lfm.initialize(this)
    }
}