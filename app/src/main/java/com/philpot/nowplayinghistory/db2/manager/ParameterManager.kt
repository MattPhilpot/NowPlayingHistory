package com.philpot.nowplayinghistory.db2.manager

import com.philpot.nowplayinghistory.coroutine.CoroutineContextProvider
import com.philpot.nowplayinghistory.db2.dao.ParameterDao
import com.philpot.nowplayinghistory.model.param.BaseParameter
import com.philpot.nowplayinghistory.model.param.BooleanParameter
import com.philpot.nowplayinghistory.model.param.CoreParameter
import com.philpot.nowplayinghistory.model.param.DateTimeParameter
import com.philpot.nowplayinghistory.model.param.IntegerParameter
import com.philpot.nowplayinghistory.model.param.Parameter
import com.philpot.nowplayinghistory.model.param.ParameterType
import com.philpot.nowplayinghistory.model.param.StringParameter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime

class ParameterManager(private val parameterDao: ParameterDao,
                       private val coroutineContextProvider: CoroutineContextProvider) {

    var ScrollToTop: Boolean
        get() = runBlocking { getBooleanParameter(ParameterType.ScrollToTop).value }
        set(value) = saveBooleanParameter(ParameterType.ScrollToTop, value)

    var CurrentMusicApp: Int
        get() = runBlocking { getIntegerParameter(ParameterType.CurrentMusicApp).value }
        set(value) = saveIntegerParameter(ParameterType.CurrentMusicApp, value)

    var LastFmIntegration: Boolean
        get() = runBlocking { getBooleanParameter(ParameterType.LastFmIntegration).value }
        set(value) = saveBooleanParameter(ParameterType.LastFmIntegration, value)

    var GPSEnable: Boolean
        get() = runBlocking { getBooleanParameter(ParameterType.GPSEnable).value }
        set(value) = saveBooleanParameter(ParameterType.GPSEnable, value)

    var FavoritesOrderFilter: Int
        get() = runBlocking { getIntegerParameter(ParameterType.FavoritesOrderFilter).value }
        set(value) = saveIntegerParameter(ParameterType.FavoritesOrderFilter, value)

    //support functions
    fun save(baseParameter: BaseParameter<*>) {
        coroutineContextProvider.ioScope.launch {
            parameterDao.insertOrUpdate(Parameter.createParameter(baseParameter))
        }
    }

    suspend fun get(type: CoreParameter, default: String = ""): BaseParameter<*> {
        val param = parameterDao.getById(type.code)
        return if (param != null) {
            BaseParameter.createParameter(type, param.value)
        } else {
            val retVal = BaseParameter.createParameter(type, default)
            save(retVal)
            retVal
        }
    }

    suspend fun getStringParameter(type: CoreParameter): StringParameter {
        return get(type) as StringParameter
    }

    suspend fun getBooleanParameter(type: CoreParameter, default: Boolean = false): BooleanParameter {
        return get(type, (if (default) 1 else 0).toString()) as BooleanParameter
    }

    suspend fun getDateTimeParameter(type: CoreParameter, defaultDateTime: DateTime?): DateTimeParameter {
        return get(type, defaultDateTime?.toString() ?: "") as DateTimeParameter
    }

    suspend fun getIntegerParameter(type: CoreParameter): IntegerParameter {
        return get(type) as IntegerParameter
    }

    fun saveBooleanParameter(type: CoreParameter, value: Boolean) {
        save(BooleanParameter(type, value))
    }

    fun saveStringParameter(type: CoreParameter, value: String?) {
        save(StringParameter(type, value ?: ""))
    }

    fun saveDateTimeParameter(type: CoreParameter, value: DateTime?, defaultIfValueNull: DateTime = DateTime.now()) {
        save(DateTimeParameter(type, value ?: defaultIfValueNull))
    }

    fun saveIntegerParameter(type: ParameterType, value: Int?) {
        save(IntegerParameter(type, value))
    }
}
