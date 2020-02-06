package com.philpot.nowplayinghistory.model.param

abstract class BaseParameter<out T>(val code: CoreParameter, val value: T) {

    companion object {
        fun createParameter(code: CoreParameter, value: String?): BaseParameter<Any> {
            return when (code.valueType) {
                IntegerParameter.REFERENCE_TYPE -> IntegerParameter(code, value)
                BooleanParameter.REFERENCE_TYPE -> BooleanParameter(code, value)
                DateTimeParameter.REFERENCE_TYPE -> DateTimeParameter(code, value)
                StringParameter.REFERENCE_TYPE -> StringParameter(code, value)
                else -> StringParameter(code, value)
            }
        }
    }
}
