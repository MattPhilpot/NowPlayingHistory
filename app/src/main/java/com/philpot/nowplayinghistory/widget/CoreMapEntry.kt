package com.philpot.nowplayinghistory.widget

class CoreMapEntry<out K, out V>(override val key: K, override val value: V) : Map.Entry<K, V>
