import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

interface CryptographyManager {
    /**
     * Get a cipher initialized for encryption using the specified key name.
     *
     * @param keyName The name of the key used for encryption.
     * @return The initialized Cipher instance.
     */
    fun getInitializedCipherForEncryption(keyName: String): Cipher

    /**
     * Get a cipher initialized for decryption using the specified key name and initialization vector.
     *
     * @param keyName The name of the key used for decryption.
     * @param initializationVector The initialization vector for decryption.
     * @return The initialized Cipher instance.
     */
    fun getInitializedCipherForDecryption(keyName: String, initializationVector: ByteArray): Cipher

    /**
     * Encrypt the plaintext using the provided cipher.
     *
     * @param plaintext The plaintext to encrypt.
     * @param cipher The initialized Cipher instance.
     * @return The CiphertextWrapper containing the ciphertext and initialization vector.
     */
    fun encryptData(plaintext: String, cipher: Cipher): CiphertextWrapper

    /**
     * Decrypt the ciphertext using the provided cipher.
     *
     * @param ciphertext The ciphertext to decrypt.
     * @param cipher The initialized Cipher instance.
     * @return The decrypted plaintext.
     */
    fun decryptData(ciphertext: ByteArray, cipher: Cipher): String

    /**
     * Persist the CiphertextWrapper to shared preferences.
     *
     * @param ciphertextWrapper The CiphertextWrapper to persist.
     * @param context The application context.
     * @param filename The shared preferences filename.
     * @param mode The shared preferences mode.
     * @param prefKey The key for storing the CiphertextWrapper in shared preferences.
     */
    fun persistCiphertextWrapperToSharedPrefs(
        ciphertextWrapper: CiphertextWrapper,
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String,
    )

    /**
     * Get the CiphertextWrapper from shared preferences.
     *
     * @param context The application context.
     * @param filename The shared preferences filename.
     * @param mode The shared preferences mode.
     * @param prefKey The key for retrieving the CiphertextWrapper from shared preferences.
     * @return The retrieved CiphertextWrapper, or null if not found.
     */
    fun getCiphertextWrapperFromSharedPrefs(
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String,
    ): CiphertextWrapper?
}

/**
 * Get an instance of CryptographyManager using the CryptographyManagerImpl implementation.
 *
 * @return An instance of CryptographyManager.
 */
fun CryptographyManager(): CryptographyManager = CryptographyManagerImpl()

/**
 * Implementation of the CryptographyManager interface.
 */
private class CryptographyManagerImpl : CryptographyManager {

    private val KEY_SIZE = 256
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getInitializedCipherForEncryption(keyName: String): Cipher {
        val cipher = getCipher()
        val secretKey = getOrCreateSecretKey(keyName)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getInitializedCipherForDecryption(
        keyName: String,
        initializationVector: ByteArray,
    ): Cipher {
        val cipher = getCipher()
        val secretKey = getOrCreateSecretKey(keyName)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, initializationVector))
        return cipher
    }

    override fun encryptData(plaintext: String, cipher: Cipher): CiphertextWrapper {
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charset.forName("UTF-8")))
        return CiphertextWrapper(ciphertext, cipher.iv)
    }

    override fun decryptData(ciphertext: ByteArray, cipher: Cipher): String {
        val plaintext = cipher.doFinal(ciphertext)
        return String(plaintext, Charset.forName("UTF-8"))
    }

    private fun getCipher(): Cipher {
        val transformation = "$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING"
        return Cipher.getInstance(transformation)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getOrCreateSecretKey(keyName: String): SecretKey {
        // If SecretKey was previously created for that keyName, then grab and return it.
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null) // Keystore must be loaded before it can be accessed
        keyStore.getKey(keyName, null)?.let { return it as SecretKey }

        // If you reach here, then a new SecretKey must be generated for that keyName
        val paramsBuilder = KeyGenParameterSpec.Builder(
            keyName, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
        paramsBuilder.apply {
            setBlockModes(ENCRYPTION_BLOCK_MODE)
            setEncryptionPaddings(ENCRYPTION_PADDING)
            setKeySize(KEY_SIZE)
            setUserAuthenticationRequired(true)
        }

        val keyGenParams = paramsBuilder.build()
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE
        )
        keyGenerator.init(keyGenParams)
        return keyGenerator.generateKey()
    }

    override fun persistCiphertextWrapperToSharedPrefs(
        ciphertextWrapper: CiphertextWrapper,
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String,
    ) {
        val json = Gson().toJson(ciphertextWrapper)
        context.getSharedPreferences(filename, mode).edit().putString(prefKey, json).apply()
    }

    override fun getCiphertextWrapperFromSharedPrefs(
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String,
    ): CiphertextWrapper? {
        val json = context.getSharedPreferences(filename, mode).getString(prefKey, null)
        return Gson().fromJson(json, CiphertextWrapper::class.java)
    }
}

/**
 * Wrapper data class for storing ciphertext and initialization vector.
 *
 * @property ciphertext The encrypted data.
 * @property initializationVector The initialization vector used during encryption.
 */
data class CiphertextWrapper(val ciphertext: ByteArray, val initializationVector: ByteArray)
