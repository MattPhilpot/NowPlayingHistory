package com.philpot.nowplayinghistory.model.param

enum class ParameterType(override val code: String,
                         override val valueType: String) : CoreParameter {

    ScrollToTop("ScrollToTop", BooleanParameter.REFERENCE_TYPE),
    CurrentMusicApp("CurrentMusicApp", IntegerParameter.REFERENCE_TYPE),
    LastFmIntegration("LastFmIntegration", BooleanParameter.REFERENCE_TYPE),
    GPSEnable("GPSEnable", BooleanParameter.REFERENCE_TYPE),
    FavoritesOrderFilter("FavoritesOrder", IntegerParameter.REFERENCE_TYPE),

    UnknownParameter("UnknownParameter", StringParameter.REFERENCE_TYPE);

    companion object {
        fun getFromCode(code: String) : CoreParameter {
            return values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: UnknownParameter
        }
    }
}