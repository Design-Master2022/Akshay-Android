package com.newsapp.newsapp.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.Observer
import com.newsapp.newsapp.R
import com.newsapp.newsapp.biomatric.BiometricPromptUtils
import com.newsapp.newsapp.biomatric.CryptographyManager
import com.newsapp.newsapp.databinding.ActivityLoginBinding
import com.newsapp.newsapp.ui.news.MainActivity
import com.newsapp.newsapp.utils.CommonSharedPreferences
import com.newsapp.newsapp.viewmodal.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var biometricPrompt: BiometricPrompt
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext,
            CommonSharedPreferences.SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CommonSharedPreferences.CIPHERTEXT_WRAPPER
        )
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel : LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = LoginViewModel(this)
        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            if (ciphertextWrapper != null) {
                binding.useBiometrics.visibility = View.VISIBLE
            } else {
                binding.useBiometrics.visibility = View.INVISIBLE
            }
        } else {
            binding.useBiometrics.visibility = View.INVISIBLE
        }

//        binding.login.setOnClickListener {
//            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//            CommonSharedPreferences.writeBoolean(CommonSharedPreferences.IS_LOGGED_IN,true)
//            startActivity(intent)
//            finish()
//        }
        binding.login.setOnClickListener {
            loginViewModel.login(
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }
        binding.useBiometrics.setOnClickListener {
            if (ciphertextWrapper != null) {
                showBiometricPromptForDecryption()
            }
        }


        loginViewModel.loginResult.observe(this, Observer {
            val loginResult = it ?: return@Observer
            if (loginResult) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                CommonSharedPreferences.writeBoolean(CommonSharedPreferences.IS_LOGGED_IN,true)
                CommonSharedPreferences.writeString(CommonSharedPreferences.TOKEN,java.util.UUID.randomUUID().toString())
                startActivity(intent)
                finish()
            }
        })

    }

    private fun showBiometricPromptForDecryption() {
        ciphertextWrapper?.let { textWrapper ->
            val secretKeyName = getString(R.string.secret_key_name)
            val cipher = cryptographyManager.getInitializedCipherForDecryption(
                secretKeyName, textWrapper.initializationVector
            )
            biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(
                    this,
                    ::decryptServerTokenFromStorage
                )
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun decryptServerTokenFromStorage(authResult: BiometricPrompt.AuthenticationResult) {
        ciphertextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {
                val plaintext =
                    cryptographyManager.decryptData(textWrapper.ciphertext, it)
//                SampleAppUser.fakeToken = plaintext

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                CommonSharedPreferences.writeBoolean(CommonSharedPreferences.IS_LOGGED_IN,true)
                startActivity(intent)
                finish()
            }
        }
    }

}