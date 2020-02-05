package com.philpot.nowplayinghistory.db2.cipher

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import net.sqlcipher.DatabaseErrorHandler
import net.sqlcipher.database.SQLiteDatabase

class SQLCipherHelper(context: Context,
                      callback: SupportSQLiteOpenHelper.Callback,
                      databaseKey: String) : SupportSQLiteOpenHelper, DatabaseErrorHandler {

    companion object {
        private const val DATABASE_NAME = "bidnum_mobile.db"
    }

    private val openHelper: SQLCipherOpenHelper
    private val dbKey: ByteArray


    init {
        SQLiteDatabase.loadLibs(context)
        dbKey = databaseKey.toByteArray()
        openHelper = SQLCipherOpenHelper(context, DATABASE_NAME, callback.version, this, callback)
    }

    override fun onCorruption(dbObj: SQLiteDatabase?) {
        openHelper.onCorruption(dbObj)
    }

    override fun getDatabaseName(): String = openHelper.databaseName

    override fun getWritableDatabase(): SupportSQLiteDatabase {
        return openHelper.getSupportWriteableDatabase(dbKey)
    }

    override fun getReadableDatabase(): SupportSQLiteDatabase {
        return openHelper.getSupportReadableDatabase(dbKey)
    }

    override fun close() {
        openHelper.close()
    }

    override fun setWriteAheadLoggingEnabled(enabled: Boolean) {
        openHelper.setWriteAheadLoggingEnabled(enabled)
    }
}