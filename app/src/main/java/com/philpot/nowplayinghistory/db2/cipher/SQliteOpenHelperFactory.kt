package com.philpot.nowplayinghistory.db2.cipher

import android.content.Context
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.philpot.nowplayinghistory.security.EncryptionKeyGenerator

class SQliteOpenHelperFactory(context: Context) : SupportSQLiteOpenHelper.Factory {

    private val databaseKey = DatabaseKeyHelper.fetchDbKey(context)

    override fun create(configuration: SupportSQLiteOpenHelper.Configuration): SupportSQLiteOpenHelper {
        return SQLCipherHelper(configuration.context, configuration.callback, databaseKey)
    }
}
