package com.newsapp.newsapp.ui.home

import CryptographyManager
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.google.android.material.button.MaterialButton
import com.newsapp.newsapp.R
import com.newsapp.newsapp.biomatric.BiometricPromptUtils
import com.newsapp.newsapp.databinding.ActivityProfileBinding
import com.newsapp.newsapp.ui.BaseActivity
import com.newsapp.newsapp.ui.login.LoginActivity
import com.newsapp.newsapp.ui.news.MainActivity
import com.newsapp.newsapp.utils.CommonSharedPreferences


class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext,
            CommonSharedPreferences.SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CommonSharedPreferences.CIPHERTEXT_WRAPPER
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        profileViewModel = ProfileViewModel()

        setupUI()

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

        val isDarkModeOn = CommonSharedPreferences.readBoolean(CommonSharedPreferences.DARK_MODE_ENABLED)
        setDarkModeSwitchState(isDarkModeOn)


    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener {
            loadDashboard()
        }

        binding.tvBiometric.setOnClickListener {
            showBiometricPromptForEncryption()
        }

        binding.tvLogout.setOnClickListener {
            logoutUser()
        }

        binding.switchMode.setOnCheckedChangeListener { _, isChecked ->
            setDarkModeSwitchState(isChecked)
            profileViewModel.switchDarkMode(isChecked)
        }

        binding.ivLang.setOnClickListener {
            showLanguageSelectionDialog()
        }

        binding.tvLanguage.setOnClickListener {
            showLanguageSelectionDialog()
        }
    }

    private fun logoutUser() {
        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
        CommonSharedPreferences.writeBoolean(CommonSharedPreferences.IS_LOGGED_IN, false)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun setDarkModeSwitchState(isDarkModeOn: Boolean) {
        binding.switchMode.isChecked = isDarkModeOn
        binding.tvDarkMode.text = getString(
            if (isDarkModeOn) R.string.disable_dark_mode
            else R.string.switch_dark_mode
        )
    }



    private fun showLanguageSelectionDialog() {
        val inflater = layoutInflater
        val layout: View =
            inflater.inflate(com.newsapp.newsapp.R.layout.change_lang_popup_layout, findViewById(com.newsapp.newsapp.R.id.container))
        val englishBtn = layout.findViewById<MaterialButton>(com.newsapp.newsapp.R.id.english_lang)
        val arabicBtn = layout.findViewById<MaterialButton>(com.newsapp.newsapp.R.id.arabic_lang)
        val builder = AlertDialog.Builder(this@ProfileActivity)
        builder.setView(layout)
        val dialog = builder.create()
        if (dialog?.isShowing == false) {
            dialog.show()
        }
        englishBtn.setOnClickListener {
            CommonSharedPreferences.writeString(CommonSharedPreferences.LANG_ID, "en")
            profileViewModel.changeLanguage("en", this@ProfileActivity)
            dialog.dismiss() }
        arabicBtn.setOnClickListener {
            CommonSharedPreferences.writeString(CommonSharedPreferences.LANG_ID, "ar")
            profileViewModel.changeLanguage("ar", this@ProfileActivity)
            dialog.dismiss() }
    }

    private fun loadDashboard(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /**
    Displays a biometric prompt for encryption if biometric authentication is available on the device.
    It checks if the device supports biometric authentication, retrieves the secret key name from resources,
    gets the initialized cipher for encryption, creates a biometric prompt, and creates the prompt info for the prompt.
    Finally, it authenticates the user with the biometric prompt using the initialized cipher.
     */
    private fun showBiometricPromptForEncryption() {
        // Check if biometric authentication is available on the device
        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            // Retrieve the secret key name from resources
            val secretKeyName = applicationContext.resources.getString(R.string.secret_key_name)

            // Get the initialized cipher for encryption
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)

            // Create a biometric prompt
            val biometricPrompt = BiometricPromptUtils.createBiometricPrompt(this, ::encryptAndStoreServerToken)

            // Create the prompt info for the biometric prompt
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)

            // Authenticate the user with the biometric prompt using the initialized cipher
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    /**

    Encrypts the server token using the provided cipher obtained from the authentication result's crypto object.
    The encrypted token wrapper is then stored in SharedPreferences for future use.
    Finally, the profile page is reloaded to reflect the updated token.
    @param authResult The authentication result containing the crypto object with the cipher.
     */
    private fun encryptAndStoreServerToken(authResult: BiometricPrompt.AuthenticationResult) {
        // Retrieve the server token from SharedPreferences
        val fakeToken = CommonSharedPreferences.readString(CommonSharedPreferences.TOKEN)

        // Get the cipher from the authentication result's crypto object
        authResult.cryptoObject?.cipher?.apply {
            // Encrypt the server token using the cipher
            fakeToken.let { token ->
                Log.d(ContentValues.TAG, "The token from the server is $token")
                val encryptedServerTokenWrapper = cryptographyManager.encryptData(token, this)

                // Persist the encrypted server token wrapper to SharedPreferences
                cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                    encryptedServerTokenWrapper,
                    applicationContext,
                    CommonSharedPreferences.SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CommonSharedPreferences.CIPHERTEXT_WRAPPER
                )
            }
        }

        // Reload the profile page to reflect the updated token
        reloadProfilePage(this)
    }

    /**
    Reloads the profile page by recreating the ProfileActivity.
    @param activity The reference to the current ProfileActivity.
     */
    private fun reloadProfilePage(activity: ProfileActivity) {
        val intent = Intent(activity, ProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intent)
        activity.finish()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        loadDashboard()
    }
}