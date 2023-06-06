package com.newsapp.newsapp.ui.home

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.ViewModel
import com.newsapp.newsapp.R
import com.newsapp.newsapp.biomatric.BiometricPromptUtils
import com.newsapp.newsapp.biomatric.CryptographyManager
import com.newsapp.newsapp.utils.CommonSharedPreferences
import java.util.*

class ProfileViewModel constructor(private val applicationContext: Context) : ViewModel() {

    private val cryptographyManager = CryptographyManager()
    val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext,
            CommonSharedPreferences.SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CommonSharedPreferences.CIPHERTEXT_WRAPPER
        )

    fun showBiometricPromptForEncryption() {
        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val secretKeyName = applicationContext.resources.getString(R.string.secret_key_name)
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt((applicationContext as ProfileActivity), ::encryptAndStoreServerToken)
            val promptInfo = BiometricPromptUtils.createPromptInfo(applicationContext)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun encryptAndStoreServerToken(authResult: BiometricPrompt.AuthenticationResult) {
        val  fakeToken = CommonSharedPreferences.readString(CommonSharedPreferences.TOKEN)
        authResult.cryptoObject?.cipher?.apply {
            fakeToken.let { token ->
                Log.d(ContentValues.TAG, "The token from server is $token")
                val encryptedServerTokenWrapper = cryptographyManager.encryptData(token, this)
                cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                    encryptedServerTokenWrapper,
                    applicationContext,
                    CommonSharedPreferences.SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CommonSharedPreferences.CIPHERTEXT_WRAPPER
                )
            }
        }
        reloadProfilePage((applicationContext as ProfileActivity))
    }

    fun changeLanguage(language: String, activity: ProfileActivity){
        var locale: Locale? = null
        if (language == "en") {
            locale = Locale("en")
        } else if (language == "ar") {
            locale = Locale("ar")
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        activity.resources.updateConfiguration(config, null)
        reloadProfilePage(activity)
    }

    fun switchDarkMode(isChecked: Boolean, activity: ProfileActivity){
        // if the button is checked, i.e., towards the right or enabled
        // enable dark mode, change the text to disable dark mode
        // else keep the switch text to enable dark mode
        if (isChecked) {
            // will turn it on
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            // it will set isDarkModeOn
            // boolean to true
            CommonSharedPreferences.writeBoolean(CommonSharedPreferences.DARK_MODE_ENABLED, true)

        } else {
            // will turn it off
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            // it will set isDarkModeOn
            // boolean to false
            CommonSharedPreferences.writeBoolean(CommonSharedPreferences.DARK_MODE_ENABLED, false)
        }

    }

    private fun reloadProfilePage(activity: ProfileActivity){
        val intent = Intent(activity, ProfileActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intent)
        activity.finish()
    }
}