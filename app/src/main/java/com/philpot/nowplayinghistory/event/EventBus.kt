package com.philpot.nowplayinghistory.event

/**
 * Created by colse on 10/30/2017.
 */
interface EventBus {
    fun post(event: kotlin.Any)
    fun register(listener: kotlin.Any)
    fun unregister(listener: kotlin.Any)
}