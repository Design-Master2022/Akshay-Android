package com.newsapp.newsapp.ui.login

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.Observer
import com.newsapp.newsapp.R
import com.newsapp.newsapp.biomatric.BiometricPromptUtils
import com.newsapp.newsapp.biomatric.CryptographyManager
import com.newsapp.newsapp.databinding.ActivityLoginBinding
import com.newsapp.newsapp.ui.BaseActivity
import com.newsapp.newsapp.ui.news.MainActivity
import com.newsapp.newsapp.utils.CommonSharedPreferences

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel : LoginViewModel
    private lateinit var biometricPrompt: BiometricPrompt
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            this,
            CommonSharedPreferences.SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CommonSharedPreferences.CIPHERTEXT_WRAPPER
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = LoginViewModel(baseContext.applicationContext as Application)


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

        //this method is check the availability of biometric capability
        checkDeviceHasBiometric()

    }

    private fun checkDeviceHasBiometric(){
        when(loginViewModel.hasBiometricCapability(applicationContext)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                if (ciphertextWrapper != null) {
                    binding.useBiometrics.visibility = View.VISIBLE
                } else {
                    binding.useBiometrics.visibility = View.INVISIBLE
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                binding.useBiometrics.visibility = View.INVISIBLE
                Toast.makeText(
                    applicationContext,
                    getString(R.string.no_fingerprint_sensor),
                    Toast.LENGTH_LONG
                ).show()

            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                binding.useBiometrics.visibility = View.INVISIBLE
                Toast.makeText(
                    applicationContext,
                    getString(R.string.biometric_sensors_unavailable),
                    Toast.LENGTH_LONG
                ).show()

            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.useBiometrics.visibility = View.INVISIBLE
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG or DEVICE_CREDENTIAL)

                }
                binding.useBiometrics.isEnabled = false
                startActivityForResult(enrollIntent, 100)
//                Toast.makeText(
//                    applicationContext,
//                    getString(R.string.finger_print_has_not_renrolled),
//                    Toast.LENGTH_LONG
//                ).show()

            }
        }
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

                val intent = Intent(this, MainActivity::class.java)
                CommonSharedPreferences.writeBoolean(CommonSharedPreferences.IS_LOGGED_IN,true)
                startActivity(intent)
                finish()
            }
        }
    }


}