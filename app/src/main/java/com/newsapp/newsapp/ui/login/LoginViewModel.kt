package com.newsapp.newsapp.ui.login

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.biometric.BiometricManager
import androidx.lifecycle.*
import com.newsapp.newsapp.R
import com.newsapp.newsapp.utils.NewsApp
import com.newsapp.newsapp.utils.Utils
import com.newsapp.newsapp.utils.Utils.showToast


class LoginViewModel(var app: Application) : AndroidViewModel(app) {

    val loginResult= MutableLiveData<Boolean>()

    fun login(email: String, password: String) {
        if(validation(email,password)){
            if(Utils.isInternetAvailable(getApplication<NewsApp>().baseContext)) {
                loginResult.value = true
            }else{
                Utils.commonInternetAlert(getApplication<NewsApp>().baseContext, getApplication<NewsApp>().baseContext.getString(R.string.internet_not_avl))
            }
        }
    }


    fun validation(email: String, password: String): Boolean {
            if (email.isEmpty()) {
                showToast(getApplication<NewsApp>().baseContext,getApplication<NewsApp>().baseContext.getString(R.string.empty_emailid))
                return false
            }
            if ( Patterns.EMAIL_ADDRESS.matcher(email).matches().not()) {
                showToast(getApplication<NewsApp>().baseContext,getApplication<NewsApp>().baseContext.getString(R.string.valid_email_id))
                return false
            }
            if (password.isEmpty()) {
                showToast(getApplication<NewsApp>().baseContext,getApplication<NewsApp>().baseContext.getString(R.string.empty_password))
                return false
            }
            if (password.length < 6) {
                showToast(getApplication<NewsApp>().baseContext,getApplication<NewsApp>().baseContext.getString(R.string.valid_password))
                return false
            }

            return true
    }

    fun hasBiometricCapability(context: Context): Int {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
    }


}