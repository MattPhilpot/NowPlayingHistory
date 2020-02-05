package com.philpot.nowplayinghistory.di

import com.philpot.nowplayinghistory.coroutine.CoroutineContextProvider
import com.philpot.nowplayinghistory.coroutine.NowPlayingCoroutineContextProvider
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton

val generalModule = Kodein.Module("GeneralModule") {

    bind<CoroutineContextProvider>() with singleton {
        NowPlayingCoroutineContextProvider()
    }
}
