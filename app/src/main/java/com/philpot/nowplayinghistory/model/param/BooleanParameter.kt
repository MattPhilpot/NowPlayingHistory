package com.philpot.nowplayinghistory.model.param

import androidx.core.text.isDigitsOnly

class BooleanParameter : BaseParameter<Boolean> {

    companion object {
        const val REFERENCE_TYPE = "BooleanParameter"

        private fun getValueFrom(value: String?): Boolean? {
            return if (value.isNullOrBlank()) {
                false
            } else if (value.isDigitsOnly() && (value == "1" || value == "0")) {
                value == "1"
            } else {
                value.toBoolean()
            }
        }
    }

    constructor(code: CoreParameter, value: Boolean? = null) : super(code, value ?: false)
    constructor(code: CoreParameter, value: String?) : this(code, getValueFrom(value))
}