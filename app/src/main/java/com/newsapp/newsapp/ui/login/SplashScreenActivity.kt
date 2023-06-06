package com.newsapp.newsapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.newsapp.newsapp.R
import com.newsapp.newsapp.ui.BaseActivity
import com.newsapp.newsapp.ui.news.MainActivity
import com.newsapp.newsapp.utils.CommonSharedPreferences

class SplashScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

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
}