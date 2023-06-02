package com.newsapp.newsapp.utils


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.newsapp.newsapp.R
import com.newsapp.newsapp.databinding.CommonAlertBinding
import java.security.AccessController.getContext
import java.security.MessageDigest
import java.util.*


object Utils {

    var progressLoading: ExtProgressDialog? = null
    fun isMobileValid(mobile: String): Boolean {
        return mobile.matches(Regex("^([0]|\\+91)?\\d{10,12}"))
    }

    fun isInternetOn(context: Context): Boolean {
        val connection =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        if (connection.getNetworkInfo(0)!!.state == NetworkInfo.State.CONNECTED || connection.getNetworkInfo(
                0
            )!!
                .getState() == NetworkInfo.State.CONNECTING || connection.getNetworkInfo(1)!!
                .getState() == NetworkInfo.State.CONNECTING || connection.getNetworkInfo(1)!!
                .getState() == NetworkInfo.State.CONNECTED
        ) {
            return true
        } else if (connection.getNetworkInfo(0)!!
                .getState() == NetworkInfo.State.DISCONNECTED
            || connection.getNetworkInfo(1)!!
                .getState() == NetworkInfo.State.DISCONNECTED
        ) {
            return false
        }
        return false
    }

    fun commonInternetAlert(context: Context, message: String) {
        var customDialog: AlertDialog? = null
        if (customDialog != null) {
            customDialog.dismiss()
        }

        val binding: CommonAlertBinding = CommonAlertBinding
            .inflate(LayoutInflater.from(context))
        var customAlertBuilder = AlertDialog.Builder(context)
        customAlertBuilder.setView(binding.getRoot())
        customDialog = customAlertBuilder.create()
        customDialog.show()

        customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.cardCustomAlertOk.setOnClickListener {
            customDialog.dismiss()
        }
    }

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun hashString(input: String): String {
        return MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

//    fun getLoadingDialog(
//        context: Context?,
//        strMessage: String?
//    ): ExtProgressDialog? {
//        if (progressLoading == null) { // context = context.getApplicationContext();
//            progressLoading =
//                ExtProgressDialog(context, R.style.dialogNoDim)
//            progressLoading?.setMessage(strMessage)
//            progressLoading?.setIndeterminate(true)
//            progressLoading?.setCanceledOnTouchOutside(false)
//            progressLoading?.setCancelable(true)
//        }
//        return progressLoading
//    }


    @JvmStatic
    fun setLocale(context: Context) {
        if (!TextUtils.isEmpty(CommonSharedPreferences.readString(CommonSharedPreferences.LANG_ID))) {
            val myLocale =
                Locale(CommonSharedPreferences.readString(CommonSharedPreferences.LANG_ID))
            val res = context.resources
            val conf = res.configuration
            conf.locale = myLocale
            res.updateConfiguration(conf, res.displayMetrics)
        } else {
            //println("LOCALE_NULL")
        }
    }

    fun isAlphaNumeric(s: String): Boolean {
        return s != null && s.matches("^[a-zA-Z0-9]*$".toRegex());
    }


}