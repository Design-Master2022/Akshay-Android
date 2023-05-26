package com.newsapp.newsapp.ui.home

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.newsapp.newsapp.R
import com.newsapp.newsapp.biomatric.BiometricPromptUtils
import com.newsapp.newsapp.biomatric.CryptographyManager
import com.newsapp.newsapp.databinding.ActivityProfileBinding
import com.newsapp.newsapp.ui.BaseActivity
import com.newsapp.newsapp.ui.login.LoginActivity
import com.newsapp.newsapp.utils.CommonSharedPreferences

class ProfileActivity : BaseActivity() {
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
            get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext,
            CommonSharedPreferences.SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CommonSharedPreferences.CIPHERTEXT_WRAPPER
        )
//    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivBack.setOnClickListener { finish() }
        binding.tvBiometric.setOnClickListener {   showBiometricPromptForEncryption() }
        binding.tvLogout.setOnClickListener {
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            CommonSharedPreferences.writeBoolean(CommonSharedPreferences.IS_LOGGED_IN,false)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            if (ciphertextWrapper != null) {

                binding.ivBiometric.visibility = View.GONE
                binding.tvBiometric.visibility = View.GONE

            } else {
                binding.ivBiometric.visibility = View.VISIBLE
                binding.tvBiometric.visibility = View.VISIBLE

            }

        } else {
            binding.ivBiometric.visibility = View.GONE
            binding.tvBiometric.visibility = View.GONE
        }
    }
    private fun showBiometricPromptForEncryption() {
        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val secretKeyName = getString(R.string.secret_key_name)
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(this, ::encryptAndStoreServerToken)
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun encryptAndStoreServerToken(authResult: BiometricPrompt.AuthenticationResult) {
//        val fakeToken = java.util.UUID.randomUUID().toString()
        val  fakeToken = CommonSharedPreferences.readString(CommonSharedPreferences.TOKEN)
//        fakeToken =
        authResult.cryptoObject?.cipher?.apply {
           fakeToken?.let { token ->
                Log.d(TAG, "The token from server is $token")
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
        finish()
    }
}