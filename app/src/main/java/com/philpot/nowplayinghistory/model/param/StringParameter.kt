package com.philpot.nowplayinghistory.model.param

class StringParameter(code: CoreParameter, value: String? = null) : BaseParameter<String>(code, value ?: "") {

    companion object {
        const val REFERENCE_TYPE = "StringParameter"
    }
}
