package com.philpot.nowplayinghistory.model.param

class IntegerParameter : BaseParameter<Int> {

    companion object {
        const val REFERENCE_TYPE = "IntegerParameter"

        private fun getValueFrom(value: String?): Int? {
            return value?.toIntOrNull() ?: 0
        }
    }

    constructor(code: CoreParameter, value: Int? = null) : super(code, value ?: 0)
    constructor(code: CoreParameter, value: String?) : this(code, getValueFrom(value))
}
