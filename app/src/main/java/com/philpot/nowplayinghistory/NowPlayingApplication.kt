package com.philpot.nowplayinghistory

import android.app.Application
import androidx.room.Room
import coil.Coil
import coil.ImageLoader
import com.philpot.nowplayinghistory.db2.NowPlayingDatabase
import com.philpot.nowplayinghistory.db2.cipher.SQliteOpenHelperFactory
import com.philpot.nowplayinghistory.db2.manager.SongAlbumManager
import com.philpot.nowplayinghistory.di.generalModule
import com.philpot.nowplayinghistory.info.AlbumArtCacheProvider
import com.philpot.nowplayinghistory.info.NowPlayingArtProvider
import com.philpot.nowplayinghistory.lastfm.Lfm
import com.philpot.nowplayinghistory.viewmodel.NowPlayingViewModelFactory
import com.squareup.leakcanary.LeakCanary
import kotlinx.coroutines.Dispatchers
import net.danlew.android.joda.JodaTimeAndroid
import okhttp3.OkHttpClient
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.direct
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import org.kodein.di.erased.singleton

/**
 * Created by colse on 10/29/2017.
 */
class NowPlayingApplication : Application(), KodeinAware {

    override val kodein by Kodein.lazy {
        import(androidXModule(this@NowPlayingApplication))
        import(generalModule)

        bind<NowPlayingDatabase>() with singleton {
            getInitializedRoom()
        }

        bind<NowPlayingViewModelFactory>() with provider {
            NowPlayingViewModelFactory(this@NowPlayingApplication)
        }

        //maybe?
        bind<AlbumArtCacheProvider>() with singleton {
            NowPlayingArtProvider(instance(), instance(), applicationContext.cacheDir.absolutePath)
        }

        bind<SongAlbumManager>() with singleton {
            SongAlbumManager(
                instance(),
                instance(),
                instance()
            )
        }
    }

    private var roomDatabase: NowPlayingDatabase? = null

    private fun getInitializedRoom(): NowPlayingDatabase {
        return roomDatabase ?: Room
            .databaseBuilder(this, NowPlayingDatabase::class.java, "bidnum_mobile.db")
            .openHelperFactory(SQliteOpenHelperFactory(this))
            .build().apply {
            roomDatabase = this
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        //initialize lastfm
        Lfm.initialize(this)

        //initialize Joda
        JodaTimeAndroid.init(this)

        val imageLoader = ImageLoader(this@NowPlayingApplication) {
            allowRgb565(true)
            availableMemoryPercentage(0.1)
            bitmapPoolPercentage(0.1)
            crossfade(true)
            //placeholder(R.drawable.icon_pinehead_normal)
            okHttpClient(direct.instance<OkHttpClient>())
            dispatcher(Dispatchers.IO)
        }

        Coil.setDefaultImageLoader(imageLoader)

        //initialize Room Database
        getInitializedRoom()
    }
}