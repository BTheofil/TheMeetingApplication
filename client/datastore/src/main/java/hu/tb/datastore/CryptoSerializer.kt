package hu.tb.datastore

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptoSerializer : Serializer<UserData> {

    private const val KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "meeting_user_data"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_SIZE = 12
    private const val TAG_SIZE_BITS = 128

    override val defaultValue: UserData = UserData()

    override suspend fun readFrom(input: InputStream): UserData =
        withContext(Dispatchers.IO) {
            val bytes = input.readBytes()
            if (bytes.isEmpty()) return@withContext defaultValue

            try {
                val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                    init(
                        Cipher.DECRYPT_MODE,
                        secretKey,
                        GCMParameterSpec(TAG_SIZE_BITS, bytes, 0, IV_SIZE)
                    )
                }
                val plain = cipher.doFinal(bytes, IV_SIZE, bytes.size - IV_SIZE)
                Json.decodeFromString<UserData>(plain.decodeToString())
            } catch (serialization: SerializationException) {
                throw CorruptionException("Unable to read user data", serialization)
            } catch (security: GeneralSecurityException) {
                throw CorruptionException("Unable to decrypt user data", security)
            } catch (truncated: IllegalArgumentException) {
                throw CorruptionException("User data file is truncated", truncated)
            }
        }

    override suspend fun writeTo(t: UserData, output: OutputStream) =
        withContext(Dispatchers.IO) {
            val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.ENCRYPT_MODE, secretKey)
            }
            output.write(cipher.iv)
            output.write(cipher.doFinal(Json.encodeToString(t).encodeToByteArray()))
        }

    private val secretKey: SecretKey by lazy { loadOrCreateKey() }

    private fun loadOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        val existing = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existing != null) return existing.secretKey

        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
            .apply {
                init(
                    KeyGenParameterSpec
                        .Builder(
                            KEY_ALIAS,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                        )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
                )
            }
            .generateKey()
    }
}

val Context.userDataStore: DataStore<UserData> by dataStore(
    fileName = "user.json",
    serializer = CryptoSerializer,
    corruptionHandler = ReplaceFileCorruptionHandler { UserData() },
)
