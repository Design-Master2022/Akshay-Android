package com.newsapp.newsapp.ui.login

import CryptographyManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import com.newsapp.newsapp.R
import com.newsapp.newsapp.biomatric.BiometricPromptUtils
import com.newsapp.newsapp.databinding.ActivityLoginBinding
import com.newsapp.newsapp.ui.BaseActivity
import com.newsapp.newsapp.ui.news.MainActivity
import com.newsapp.newsapp.utils.CommonSharedPreferences
import com.newsapp.newsapp.utils.Utils.showToast

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            this,
            CommonSharedPreferences.SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CommonSharedPreferences.CIPHERTEXT_WRAPPER
        )

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel.loginValidationResult.observe(this) { validationResult ->
            when (validationResult) {
                is LoginViewModel.LoginValidationResult.EmptyUsername -> {
                    // Handle empty username error
                    showToast(baseContext, getString(R.string.empty_emailid))
                }
                is LoginViewModel.LoginValidationResult.InvalidUserName -> {
                    // Handle invalid username error
                    showToast(baseContext, getString(R.string.valid_email_id))
                }
                is LoginViewModel.LoginValidationResult.EmptyPassword -> {
                    // Handle empty password error
                    showToast(baseContext, getString(R.string.empty_password))
                }
                is LoginViewModel.LoginValidationResult.InvalidPassword -> {
                    // Handle invalid password error
                    showToast(baseContext, getString(R.string.valid_password))
                }
                is LoginViewModel.LoginValidationResult.Success -> {
                    // Proceed with login
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    CommonSharedPreferences.writeBoolean(CommonSharedPreferences.IS_LOGGED_IN,true)
                    CommonSharedPreferences.writeString(CommonSharedPreferences.TOKEN,java.util.UUID.randomUUID().toString())
                    startActivity(intent)
                    finish()
                }
                else -> {
                    showToast(baseContext, getString(R.string.an_error_occured))
                }
            }
        }


        binding.login.setOnClickListener {
                loginViewModel.validateLogin(
                    binding.username.text.toString(),
                    binding.password.text.toString()
                )

        }
        binding.useBiometrics.setOnClickListener {
                if (ciphertextWrapper != null) {
                    showBiometricPromptForDecryption()
                }
        }

        //this method is check the availability of biometric capability
        checkDeviceHasBiometric()

    }

    private fun checkDeviceHasBiometric() {
        // Check the biometric capability using the ViewModel
        when (loginViewModel.hasBiometricCapability(this)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                if (ciphertextWrapper != null) {
                    binding.useBiometrics.visibility = View.VISIBLE
                } else {
                    binding.useBiometrics.visibility = View.INVISIBLE
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                binding.useBiometrics.visibility = View.INVISIBLE
                // Display a toast indicating no fingerprint sensor
                Toast.makeText(
                    applicationContext,
                    getString(R.string.no_fingerprint_sensor),
                    Toast.LENGTH_LONG
                ).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                binding.useBiometrics.visibility = View.INVISIBLE
                // Display a toast indicating unavailable biometric sensors
                Toast.makeText(
                    applicationContext,
                    getString(R.string.biometric_sensors_unavailable),
                    Toast.LENGTH_LONG
                ).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.useBiometrics.visibility = View.INVISIBLE
                // Create an intent to launch biometric enrollment
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                }
                // Disable the useBiometrics button and start the enrollment activity
                binding.useBiometrics.isEnabled = false
                startActivityForResult(enrollIntent, 100)
            }
        }
    }

    /**
    Shows the biometric prompt for decryption if a ciphertext wrapper is available.
    The method initializes the cipher for decryption, creates a BiometricPrompt instance,
    creates the prompt info for the biometric prompt, and then initiates the authentication
    with the biometric prompt using the provided cipher.
    If a ciphertext wrapper is available, the decryption process will be triggered by
    authenticating with the biometric prompt. Otherwise, the method will have no effect.
     */
    private fun showBiometricPromptForDecryption() {
        ciphertextWrapper?.let { textWrapper ->
            val secretKeyName = getString(R.string.secret_key_name)

            // Get the cipher for decryption
            val cipher = cryptographyManager.getInitializedCipherForDecryption(
                secretKeyName, textWrapper.initializationVector
            )

            // Create a BiometricPrompt instance
            biometricPrompt = BiometricPromptUtils.createBiometricPrompt(
                this,
                ::decryptServerTokenFromStorage
            )

            // Create the prompt info for the biometric prompt
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)

            // Authenticate with the biometric prompt using the cipher
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    /**
    Decrypts the server token from storage using the provided authentication result.
    The method decrypts the ciphertext using the cipher obtained from the authentication result,
    performs necessary actions with the decrypted plaintext (e.g., storing the token in a user object or performing login logic),
    and then starts the MainActivity, marking the user as logged in.
    @param authResult The authentication result from the biometric prompt.
     */
    private fun decryptServerTokenFromStorage(authResult: BiometricPrompt.AuthenticationResult) {
        ciphertextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let { cipher ->
                // Decrypt the ciphertext using the provided cipher
                val plaintext = cryptographyManager.decryptData(textWrapper.ciphertext, cipher)

                // Perform necessary actions with the decrypted plaintext
                // For example, store the plaintext token in a user object or perform login logic

                // Start the MainActivity and mark the user as logged in
                val intent = Intent(this, MainActivity::class.java)
                CommonSharedPreferences.writeBoolean(CommonSharedPreferences.IS_LOGGED_IN, true)
                startActivity(intent)
                finish()
            }
        }
    }

}