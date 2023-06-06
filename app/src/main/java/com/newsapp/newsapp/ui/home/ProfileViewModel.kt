package com.newsapp.newsapp.ui.home

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import com.newsapp.newsapp.utils.CommonSharedPreferences
import java.util.*

class ProfileViewModel : ViewModel() {

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

    fun switchDarkMode(isChecked: Boolean){
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