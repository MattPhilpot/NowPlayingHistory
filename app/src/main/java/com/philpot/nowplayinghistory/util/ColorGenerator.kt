package com.philpot.nowplayinghistory.util

import java.util.*

/**
 * Created by MattPhilpot on 11/29/2017.
 */
enum class ColorGenerator(private val list: List<Long>, private val random: Random = Random()) {
    DEFAULT(arrayListOf(0xfff16364, 0xfff58559, 0xfff9a43e, 0xffe4c62e, 0xff67bf74, 0xff59a2be, 0xff2093cd, 0xffad62a7, 0xff805781)),
    MATERIAL(arrayListOf(0xffe57373, 0xfff06292, 0xffba68c8, 0xff9575cd, 0xff7986cb, 0xff64b5f6, 0xff4fc3f7, 0xff4dd0e1,
            0xff4db6ac, 0xff81c784, 0xffaed581, 0xffff8a65, 0xffd4e157, 0xffffd54f, 0xffffb74d, 0xffa1887f, 0xff90a4ae));

    fun getRandomColor(): Long {
        return list[random.nextInt(list.size)]
    }
}