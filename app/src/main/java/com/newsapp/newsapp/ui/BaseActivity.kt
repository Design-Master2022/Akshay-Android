package com.newsapp.newsapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.newsapp.newsapp.R
import com.newsapp.newsapp.server.InternetStatus
import com.newsapp.newsapp.server.Response
import com.newsapp.newsapp.utils.ExtProgressDialog
import java.util.*


abstract class BaseActivity : AppCompatActivity() {

    var progressLoading: ExtProgressDialog? = null
    var isInternetAvailable = true
    var isCaptain = false
    private var service: Intent? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Utils.setLocale(this)
        progressLoading = getLoadingDialog(this, getString(R.string.loading))

    }

    override fun onStart() {
        super.onStart()

//        AppImpl.internetStatus.observe(this, networkStatusListener)
    }

    override fun onPause() {
        super.onPause()
//        AppImpl.internetStatus.removeObserver(networkStatusListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        progressLoading?.dismiss()
    }

    override fun onResume() {
        super.onResume()
    }

//    override fun onWindowFocusChanged(hasFocus: Boolean) {
////        super.onWindowFocusChanged(hasFocus)
//        if (hasFocus) hideSystemUI()
//    }

    // This function is to hide system navigation bar
    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }

    fun getLoadingDialog(
        context: Context?,
        strMessage: String?
    ): ExtProgressDialog? {
        if (progressLoading == null) { // context = context.getApplicationContext();
            progressLoading =
                ExtProgressDialog(context, R.style.dialogNoDim)
            progressLoading?.setMessage(strMessage)
            progressLoading?.setIndeterminate(true)
            progressLoading?.setCanceledOnTouchOutside(false)
            progressLoading?.setCancelable(true)
        }
        return progressLoading
    }

    private val networkStatusListener = Observer<InternetStatus> {
        when (it) {
            InternetStatus.AVAILABLE -> {
                // On internet available checking request status if it is in error state requesting again
                isInternetAvailable = true
//                dismissSnackBar()
            }
            InternetStatus.LOST, InternetStatus.UNAVAILABLE -> {
                isInternetAvailable = false
//                showSnackBAr(getString(R.string.no_internet)){}
            }
            else -> {
            }
        }
    }

    fun <T> networkObserver() = Observer<Response<T>> {
        handleNetworkResponse(it)
    }

    open fun <T> handleNetworkResponse(response: Response<T>?) {}



}