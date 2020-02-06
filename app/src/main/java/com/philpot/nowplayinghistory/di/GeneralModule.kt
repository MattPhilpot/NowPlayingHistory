package com.philpot.nowplayinghistory.di

import com.philpot.nowplayinghistory.NowPlayingApplication
import com.philpot.nowplayinghistory.coroutine.CoroutineContextProvider
import com.philpot.nowplayinghistory.coroutine.NowPlayingCoroutineContextProvider
import com.philpot.nowplayinghistory.history2.History2ListAdapter
import com.philpot.nowplayinghistory.repo.HistoryListRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import org.kodein.di.erased.singleton
import java.util.concurrent.TimeUnit

val generalModule = Kodein.Module("GeneralModule") {

    bind<CoroutineContextProvider>() with singleton {
        NowPlayingCoroutineContextProvider()
    }

    bind<OkHttpClient>() with singleton {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(NowPlayingApplication.DEFAULT_NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
        builder.connectTimeout(NowPlayingApplication.DEFAULT_NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)

        /*
        if (BuildConfig.DEBUG) {

        }
        */
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logger)

        builder.build()
    }

    bind<HistoryListRepository>() with singleton {
        HistoryListRepository(historyDao = instance(), coroutineContextProvider = instance())
    }
}
