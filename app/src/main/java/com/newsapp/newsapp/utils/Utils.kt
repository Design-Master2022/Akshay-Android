package com.newsapp.newsapp.utils


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import com.newsapp.newsapp.R
import com.newsapp.newsapp.databinding.CommonAlertBinding
import java.security.MessageDigest
import java.util.*


object Utils {

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

    @JvmStatic
    fun showSnackbar(
        message: String?,
        coordinatorLayout: View?,
        duration: Int
    ) {
        var message = message
        if (coordinatorLayout == null) throw NullPointerException("coordinatorLayout is null for snack bar")
        if (message == null) message = ""
        val snackbar = Snackbar
            .make(coordinatorLayout, message, duration)

        // Changing message text color
        snackbar.setActionTextColor(Color.RED)

        // Changing action button text color
        val sbView = snackbar.view
        if (sbView.layoutParams is FrameLayout.LayoutParams) {
            val params = sbView.layoutParams as FrameLayout.LayoutParams
            val sideMargin = sbView.context.resources.getDimension(R.dimen.dimen_10).toInt()
            val bottomMargin = sbView.context.resources.getDimension(R.dimen.dimen_10).toInt()
            params.setMargins(
                params.leftMargin + sideMargin,
                params.topMargin,
                params.rightMargin + sideMargin,
                params.bottomMargin + bottomMargin
            )
            sbView.layoutParams = params
        } else {
            val params = sbView.layoutParams as CoordinatorLayout.LayoutParams
            val sideMargin = sbView.context.resources.getDimension(R.dimen.dimen_10).toInt()
            val bottomMargin = sbView.context.resources.getDimension(R.dimen.dimen_10).toInt()
            params.setMargins(
                params.leftMargin + sideMargin,
                params.topMargin,
                params.rightMargin + sideMargin,
                params.bottomMargin + bottomMargin
            )
            sbView.layoutParams = params
        }
        sbView.setBackgroundResource(R.drawable.snackbar_bg_selector)
        val textView = sbView.findViewById<TextView>(R.id.snackbar_text)
        val textColor = coordinatorLayout.context.resolveAttributeColor(R.attr.text_color)
        // Set the color to the view
        textView.setTextColor(textColor)
        textView.setTextAppearance(coordinatorLayout.context, R.style.SnackbarTextStyle)
        snackbar.show()
    }

    fun Context.resolveAttributeColor(@AttrRes attrResId: Int): Int {
        val typedValue = obtainStyledAttributes(intArrayOf(attrResId))
        return try {
            typedValue.getColor(0, 0)
        } finally {
            typedValue.recycle()
        }
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