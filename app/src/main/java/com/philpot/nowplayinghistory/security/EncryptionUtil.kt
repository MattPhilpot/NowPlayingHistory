package com.philpot.nowplayinghistory.security

import android.content.Context
import com.philpot.nowplayinghistory.util.ExceptionUtil
import java.io.IOException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException

object EncryptionUtil {
    private val TAG = EncryptionUtil::class.java.simpleName

    private val keyStore: KeyStore?
        get() {
            return try {
                KeyStore.getInstance(EncryptionKeyGenerator.ANDROID_KEY_STORE).apply {
                    load(null)
                }
            } catch (e: KeyStoreException) {
                ExceptionUtil.logWithAnalytics(TAG, e)
                null
            } catch (e: CertificateException) {
                ExceptionUtil.logWithAnalytics(TAG, e)
                null
            } catch (e: NoSuchAlgorithmException) {
                ExceptionUtil.logWithAnalytics(TAG, e)
                null
            } catch (e: IOException) {
                ExceptionUtil.logWithAnalytics(TAG, e)
                null
            }
        }

    fun encrypt(context: Context, token: String): String? {
        return getSecurityKey(context)?.encrypt(token)
    }

    fun decrypt(context: Context, token: String): String? {
        return getSecurityKey(context)?.decrypt(token)
    }

    private fun getSecurityKey(context: Context): SecurityKey? {
        return EncryptionKeyGenerator.generateSecretKey(keyStore)
    }

    fun clear() {
        try {
            keyStore?.let {
                if (it.containsAlias(EncryptionKeyGenerator.KEY_ALIAS)) {
                    it.deleteEntry(EncryptionKeyGenerator.KEY_ALIAS)
                }
            }
        } catch (e: KeyStoreException) {
            ExceptionUtil.logWithAnalytics(TAG, e)
        }
    }
}
