package com.philpot.nowplayinghistory.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.philpot.nowplayinghistory.util.ExceptionUtil
import java.security.*
import javax.crypto.KeyGenerator

object EncryptionKeyGenerator {
    private val TAG = EncryptionKeyGenerator::class.java.simpleName

    internal const val ANDROID_KEY_STORE = "AndroidKeyStore"
    internal const val KEY_ALIAS = "ACloudGuruKeyAlias"

    internal fun generateSecretKey(keyStore: KeyStore?): SecurityKey? {
        try {
            if (keyStore != null && !keyStore.containsAlias(KEY_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
                keyGenerator.init(
                    KeyGenParameterSpec.Builder(KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).setBlockModes(
                        KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setRandomizedEncryptionRequired(false)
                        .build())
                return SecurityKey(keyGenerator.generateKey())
            }
        } catch (e: KeyStoreException) {
            ExceptionUtil.logWithAnalytics(TAG, e)
        } catch (e: NoSuchProviderException) {
            ExceptionUtil.logWithAnalytics(TAG, e)
        } catch (e: NoSuchAlgorithmException) {
            ExceptionUtil.logWithAnalytics(TAG, e)
        } catch (e: InvalidAlgorithmParameterException) {
            ExceptionUtil.logWithAnalytics(TAG, e)
        }

        try {
            val entry = keyStore?.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
            return SecurityKey(entry.secretKey)
        } catch (e: KeyStoreException) {
            ExceptionUtil.logWithAnalytics(TAG, e)
        } catch (e: NoSuchAlgorithmException) {
            ExceptionUtil.logWithAnalytics(TAG, e)
        } catch (e: UnrecoverableEntryException) {
            ExceptionUtil.logWithAnalytics(TAG, e)
        }

        return null
    }
}
