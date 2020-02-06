package com.philpot.nowplayinghistory.model.param

import android.util.Log
import org.joda.time.DateTime

class DateTimeParameter : BaseParameter<DateTime> {

    companion object {
        const val REFERENCE_TYPE = "DateTimeParameter"

        private fun getValueFrom(value: String?): DateTime? {
            return try {
                DateTime.parse(value)
            } catch (e: IllegalArgumentException) {
                Log.e(REFERENCE_TYPE, "Failed to parse DateTime[$value]", e)
                DateTime.now().minusMonths(1)
            }
        }
    }

    constructor(code: CoreParameter, value: DateTime? = null) : super(code, value ?: DateTime.now())
    constructor(code: CoreParameter, value: String?) : this(code, getValueFrom(value))
}
