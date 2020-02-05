package com.philpot.nowplayinghistory.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

interface CoroutineContextProvider {
    val mainScope: CoroutineScope
    val ioScope: CoroutineScope


    val ioContext: CoroutineContext
    val mainContext: CoroutineContext
}
