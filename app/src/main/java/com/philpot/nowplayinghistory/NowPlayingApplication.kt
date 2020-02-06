package com.philpot.nowplayinghistory

import android.app.Application
import coil.Coil
import coil.ImageLoader
import com.philpot.nowplayinghistory.db2.NowPlayingDatabase
import com.philpot.nowplayinghistory.di.databaseModule
import com.philpot.nowplayinghistory.di.generalModule
import com.philpot.nowplayinghistory.lastfm.Lfm
import com.philpot.nowplayinghistory.viewmodel.NowPlayingViewModelFactory
import com.squareup.leakcanary.LeakCanary
import kotlinx.coroutines.Dispatchers
import net.danlew.android.joda.JodaTimeAndroid
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.erased.bind
import org.kodein.di.erased.provider
import org.kodein.di.erased.singleton

/**
 * Created by colse on 10/29/2017.
 */
class NowPlayingApplication : Application(), KodeinAware {

    companion object {
        const val DEFAULT_NETWORK_TIMEOUT = 60000L
    }

    override val kodein by Kodein.lazy {
        import(androidXModule(this@NowPlayingApplication))
        import(generalModule)
        import(databaseModule)

        bind<NowPlayingDatabase>() with singleton {
            NowPlayingDatabase.getInstance(this@NowPlayingApplication)
        }

        bind<NowPlayingViewModelFactory>() with provider {
            NowPlayingViewModelFactory(this@NowPlayingApplication)
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
            //okHttpClient(direct.instance<OkHttpClient>())
            dispatcher(Dispatchers.IO)
        }

        Coil.setDefaultImageLoader(imageLoader)
    }
}