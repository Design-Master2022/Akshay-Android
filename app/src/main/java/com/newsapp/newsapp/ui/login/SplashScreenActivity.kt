package com.newsapp.newsapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.newsapp.newsapp.R
import com.newsapp.newsapp.ui.BaseActivity
import com.newsapp.newsapp.ui.news.MainActivity
import com.newsapp.newsapp.utils.CommonSharedPreferences

class SplashScreenActivity : BaseActivity() {
    private companion object {
        private const val SPLASH_DELAY_MS = 2500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                val isLoggedIn = CommonSharedPreferences.readBoolean(CommonSharedPreferences.IS_LOGGED_IN)
                val destinationClass = if (isLoggedIn) MainActivity::class.java else LoginActivity::class.java
                startActivity(Intent(this@SplashScreenActivity, destinationClass))
                finish()
            }, SPLASH_DELAY_MS)
        }
    }
}