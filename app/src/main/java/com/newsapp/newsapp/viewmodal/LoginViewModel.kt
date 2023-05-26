package com.newsapp.newsapp.viewmodal

import android.content.Context
import android.util.Patterns
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.newsapp.newsapp.R
import com.newsapp.newsapp.ui.login.LoginActivity
import com.newsapp.newsapp.utils.Utils
import com.newsapp.newsapp.utils.Utils.showToast


class LoginViewModel(var context: Context) : ViewModel() {

    val loginResult= MutableLiveData<Boolean>()

    fun login(email: String, password: String) {
        if(validation(email,password)){
            if(Utils.isInternetOn(context)) {
                loginResult.value = true
            }else{
                Utils.commonInternetAlert(context, context.getString(R.string.internet_not_avl))
            }
        }
    }


    private fun validation(email: String, password: String): Boolean {
            if (email?.isEmpty() == true) {
                showToast(context,context.getString(R.string.empty_emailid))
                return false
            }
            if ( Patterns.EMAIL_ADDRESS.matcher(email).matches().not()) {
                showToast(context,context.getString(R.string.valid_email_id))
                return false
            }
            if (password.isEmpty() == true) {
                showToast(context,context.getString(R.string.empty_password))
                return false
            }
            if (password.length!! < 6) {
                showToast(context,context.getString(R.string.valid_password))
                return false
            }

            return true
    }



}