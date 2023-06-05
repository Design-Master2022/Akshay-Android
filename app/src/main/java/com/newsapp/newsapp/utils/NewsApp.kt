package com.newsapp.newsapp.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import okhttp3.Cache

class NewsApp : Application() {

    var cacheSize = 10 * 1024 * 1024 // 10 MB

    override fun onCreate() {
        super.onCreate()
        CommonSharedPreferences.initialize(this)
    }

    fun getCacheDirectory() : Cache? {
        return this.cacheDir?.let { Cache(it, cacheSize.toLong()) }
    }

    fun hasNetwork(): Boolean? {
        var isConnected: Boolean? = false // Initial Value
        val connectivityManager = this.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }
}