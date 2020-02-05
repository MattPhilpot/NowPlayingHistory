package com.philpot.nowplayinghistory.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class NowPlayingCoroutineContextProvider : CoroutineContextProvider {

    override val mainScope: CoroutineScope by lazy { CoroutineScope(mainContext) }
    override val ioScope: CoroutineScope by lazy { CoroutineScope(ioContext) }

    override val mainContext: CoroutineContext = Dispatchers.Main
    override val ioContext: CoroutineContext = Dispatchers.IO
}
