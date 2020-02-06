package com.philpot.nowplayinghistory.model.param

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This is only used by the Dao. Do not use it otherwise
 */
@Entity(tableName = "Parameters")
data class Parameter(@PrimaryKey var id: String,
                     @ColumnInfo(name ="valueType") var valueType: String,
                     @ColumnInfo(name = "value") var value: String) {

    companion object {
        fun createParameter(parameter: BaseParameter<*>): Parameter {
            return Parameter(
                parameter.code.code,
                parameter.code.valueType,
                parameter.value?.toString() ?: ""
            )
        }
    }
}