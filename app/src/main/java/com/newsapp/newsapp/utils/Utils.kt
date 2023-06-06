package com.newsapp.newsapp.utils


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.newsapp.newsapp.databinding.CommonAlertBinding
import okhttp3.Cache
import java.security.MessageDigest
import java.util.*


object Utils {

    fun isMobileValid(mobile: String): Boolean {
        return mobile.matches(Regex("^([0]|\\+91)?\\d{10,12}"))
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    fun commonInternetAlert(context: Context, message: String) {
        var customDialog: AlertDialog? = null
        customDialog?.dismiss()

        val binding: CommonAlertBinding = CommonAlertBinding
            .inflate(LayoutInflater.from(context))
        val customAlertBuilder = AlertDialog.Builder(context)
        customAlertBuilder.setView(binding.root)
        customDialog = customAlertBuilder.create()
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.tvCustomAlertMessage.text= message
        binding.cardCustomAlertOk.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun hashString(input: String): String {
        return MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }


    @JvmStatic
    fun setLocale(context: Context) {
        if (!TextUtils.isEmpty(CommonSharedPreferences.readString(CommonSharedPreferences.LANG_ID))) {
            val myLocale =
                Locale(CommonSharedPreferences.readString(CommonSharedPreferences.LANG_ID))
            setLanguage(context, myLocale)
        } else {
            //println("LOCALE_NULL")
            val myLocale =
                Locale(Locale.getDefault().language)
            CommonSharedPreferences.writeString(CommonSharedPreferences.LANG_ID, myLocale.toString())
            setLanguage(context, myLocale)
        }
    }

    private fun setLanguage(context: Context, selectedLang: Locale){

        val res = context.resources
        val conf = res.configuration
        conf.locale = selectedLang
        res.updateConfiguration(conf, res.displayMetrics)
    }

    fun isAlphaNumeric(s: String): Boolean {
        return s != null && s.matches("^[a-zA-Z0-9]*$".toRegex());
    }

}