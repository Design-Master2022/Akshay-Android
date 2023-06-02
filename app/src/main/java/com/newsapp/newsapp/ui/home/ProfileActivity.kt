package com.newsapp.newsapp.ui.home

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat.startActivity
import com.newsapp.newsapp.R
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
        profileViewModel = ProfileViewModel(baseContext.applicationContext)

        // Saving state of our app
        // using SharedPreferences
        val isDarkModeOn = CommonSharedPreferences.readBoolean(CommonSharedPreferences.DARK_MODE_ENABLED)
//        val selectedLanguage = CommonSharedPreferences.readInt(CommonSharedPreferences.LANG_ID)

        binding.ivBack.setOnClickListener { finish() }
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

        binding.spLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                if (position == 0) {
                    return
                }
                val lang = parent?.getItemAtPosition(position) as String
                lang.let {
                    if (it == resources.getString(R.string.english)) {
                        CommonSharedPreferences.writeString(CommonSharedPreferences.LANG_ID, "en")
                        profileViewModel.changeLanguage("en", this@ProfileActivity)
                    } else {
                        CommonSharedPreferences.writeString(CommonSharedPreferences.LANG_ID, "ar")
                        profileViewModel.changeLanguage("ar", this@ProfileActivity)
                    }
                }

            }
        }

        // set the switch to listen on checked change
        binding.switchMode.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                binding.tvDarkMode.text = getString(R.string.disable_dark_mode)
            } else {
                binding.tvDarkMode.text = resources.getString(R.string.switch_dark_mode)
            }
            profileViewModel.switchDarkMode(isChecked, this)
        }


        // When user reopens the app
        // after applying dark/light mode
        if (isDarkModeOn) {
            binding.tvDarkMode.text = getString(R.string.disable_dark_mode)
            binding.switchMode.isChecked = true
        } else {
            binding.tvDarkMode.text = getString(R.string.switch_dark_mode)
            binding.switchMode.isChecked = false
        }
//        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
//            setTheme(R.style.darkTheme) //when dark mode is enabled, we use the dark theme
//        } else {
//            setTheme(R.style.AppTheme)  //default app theme
//        }

//        if (selectedLanguage != 0) {
//            binding.spLang.setSelection(selectedLanguage)
//        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}