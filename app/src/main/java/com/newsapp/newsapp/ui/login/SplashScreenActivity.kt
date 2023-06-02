package com.newsapp.newsapp.ui.login

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil.setContentView
import com.newsapp.newsapp.R
import com.newsapp.newsapp.ui.BaseActivity
import com.newsapp.newsapp.ui.home.ProfileActivity
import com.newsapp.newsapp.ui.news.MainActivity
import com.newsapp.newsapp.utils.CommonSharedPreferences
import java.util.*

class SplashScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

//        // Saving state of our app
//        // using SharedPreferences
//        val isDarkModeOn= CommonSharedPreferences.readBoolean(CommonSharedPreferences.DARK_MODE_ENABLED)
//        val selectedLanguage= CommonSharedPreferences.readInt(CommonSharedPreferences.LANG_ID)
//        selectedLanguage.let {
//            if(it == 0) {
//                if (it == 1) {
//                    changeLanguage("en", this)
//                } else {
//                    changeLanguage("ar", this)
//                }
//            } else {
//                changeLanguage(Locale.getDefault().language, this)
//            }
//        }
//
//        // When user reopens the app
//        // after applying dark/light mode
//        if (isDarkModeOn) {
//            AppCompatDelegate
//                .setDefaultNightMode(
//                    AppCompatDelegate
//                        .MODE_NIGHT_YES)
//        }
//        else {
//            AppCompatDelegate
//                .setDefaultNightMode(
//                    AppCompatDelegate
//                        .MODE_NIGHT_NO);
//        }

        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                val isLoggedIn = CommonSharedPreferences.readBoolean(CommonSharedPreferences.IS_LOGGED_IN)
                if (isLoggedIn) {
                    startActivity(
                        Intent(
                            this@SplashScreenActivity,
                            MainActivity::class.java
                        )
                    )
                    finish()
                } else {
                    startActivity(
                        Intent(
                            this@SplashScreenActivity,
                            LoginActivity::class.java
                        )
                    )
                    finish()
                }

            }, 3000L)
        }
    }

    private fun changeLanguage(language: String, activity: AppCompatActivity){
        var locale: Locale? = null
        if (language == "en") {
            locale = Locale("en")
        } else if (language.equals("ar")) {
            locale = Locale("ar")
        }
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        activity.resources.updateConfiguration(config, null)
    }
}