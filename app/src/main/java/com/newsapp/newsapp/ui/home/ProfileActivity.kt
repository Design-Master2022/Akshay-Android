package com.newsapp.newsapp.ui.home

import android.R
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import com.google.android.material.button.MaterialButton
import com.newsapp.newsapp.databinding.ActivityProfileBinding
import com.newsapp.newsapp.ui.BaseActivity
import com.newsapp.newsapp.ui.login.LoginActivity
import com.newsapp.newsapp.ui.news.MainActivity
import com.newsapp.newsapp.utils.CommonSharedPreferences
import java.util.*


class ProfileActivity : BaseActivity() {

    //    private val profileViewModel by viewModels<ProfileViewModel>()
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        profileViewModel = ProfileViewModel(this@ProfileActivity)

        // Saving state of our app
        // using SharedPreferences
        val isDarkModeOn = CommonSharedPreferences.readBoolean(CommonSharedPreferences.DARK_MODE_ENABLED)

        binding.ivBack.setOnClickListener {
            loadDashboard()
        }
        binding.tvBiometric.setOnClickListener { profileViewModel.showBiometricPromptForEncryption() }
        binding.tvLogout.setOnClickListener {
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            CommonSharedPreferences.writeBoolean(CommonSharedPreferences.IS_LOGGED_IN, false)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            if (profileViewModel.ciphertextWrapper != null) {

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



        // set the switch to listen on checked change
        binding.switchMode.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                binding.tvDarkMode.text = getString(com.newsapp.newsapp.R.string.disable_dark_mode)
            } else {
                binding.tvDarkMode.text = resources.getString(com.newsapp.newsapp.R.string.switch_dark_mode)
            }
            profileViewModel.switchDarkMode(isChecked, this)
        }

        binding.ivLang.setOnClickListener{
            showLanguageSelectionDialog()
        }

        binding.tvLanguage.setOnClickListener{
            showLanguageSelectionDialog()
        }


        // When user reopens the app
        // after applying dark/light mode
        if (isDarkModeOn) {
            binding.tvDarkMode.text = getString(com.newsapp.newsapp.R.string.disable_dark_mode)
            binding.switchMode.isChecked = true
        } else {
            binding.tvDarkMode.text = getString(com.newsapp.newsapp.R.string.switch_dark_mode)
            binding.switchMode.isChecked = false
        }


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

    override fun onBackPressed() {
        super.onBackPressed()
        loadDashboard()
    }
}