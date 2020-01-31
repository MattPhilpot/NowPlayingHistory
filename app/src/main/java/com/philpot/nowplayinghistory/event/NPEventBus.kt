package com.philpot.nowplayinghistory.event

/**
 * Created by colse on 10/30/2017.
 */
class NPEventBus(private val eventBus: org.greenrobot.eventbus.EventBus) : EventBus {

    override fun post(event: Any) {
        eventBus.post(event)
    }

    override fun register(listener: Any) {
        if (!eventBus.isRegistered(listener)) {
            eventBus.register(listener)
        }
    }

    override fun unregister(listener: Any) {
        if (eventBus.isRegistered(listener)) {
            eventBus.unregister(listener)
        }
    }
}