package com.newsapp.newsapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.newsapp.newsapp.R
import com.newsapp.newsapp.utils.CommonSharedPreferences
import com.newsapp.newsapp.utils.Utils


abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Saving state of our app
        // using SharedPreferences
        val isDarkModeOn = CommonSharedPreferences.readBoolean(CommonSharedPreferences.DARK_MODE_ENABLED)
        // When user reopens the app
        // after applying dark/light mode
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darkTheme) //when dark mode is enabled, we use the dark theme
        } else {
            setTheme(R.style.AppTheme)  //default app theme
        }
        super.onCreate(savedInstanceState)
           Utils.setLocale(this)
    }


}