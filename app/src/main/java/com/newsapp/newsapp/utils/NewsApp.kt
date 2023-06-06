package com.newsapp.newsapp.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import okhttp3.Cache

class NewsApp : Application() {

    private val cacheSize = 10 * 1024 * 1024 // 10 MB

    override fun onCreate() {
        super.onCreate()
        CommonSharedPreferences.initialize(this)
    }

    /**
     * Get the cache directory for storing cached data.
     *
     * @return The Cache object representing the cache directory.
     */
    fun getCacheDirectory(): Cache? {
        return cacheDir?.let { Cache(it, cacheSize.toLong()) }
    }

    /**
     * Check if the device has an active network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    fun hasNetwork(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected ?: false
    }
}