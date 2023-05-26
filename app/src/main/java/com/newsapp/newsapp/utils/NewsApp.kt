package com.newsapp.newsapp.utils

import android.app.Application

class NewsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        CommonSharedPreferences.initialize(this)
    }
}