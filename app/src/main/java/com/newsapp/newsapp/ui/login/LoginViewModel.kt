package com.newsapp.newsapp.ui.login

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.biometric.BiometricManager
import androidx.lifecycle.*


class LoginViewModel(var app: Application) : AndroidViewModel(app) {

    private val _loginValidationResult = MutableLiveData<LoginValidationResult>()
    val loginValidationResult: LiveData<LoginValidationResult> = _loginValidationResult

    fun validateLogin(username: String, password: String) {
        // Perform validation checks and store the result in validationResult
        val validationResult = when {
            username.isEmpty() -> LoginValidationResult.EmptyUsername // Username is empty
            Patterns.EMAIL_ADDRESS.matcher(username).matches().not() -> LoginValidationResult.InvalidUserName // Invalid username format
            password.isEmpty() -> LoginValidationResult.EmptyPassword // Password is empty
            password.length < 6 -> LoginValidationResult.InvalidPassword // Password length is less than 6 characters
            else -> LoginValidationResult.Success // Login validation success
        }

        // Update the loginValidationResult LiveData with the validationResult
        _loginValidationResult.value = validationResult
    }

    sealed class LoginValidationResult {
        object EmptyUsername : LoginValidationResult()
        object EmptyPassword : LoginValidationResult()
        object InvalidUserName : LoginValidationResult()
        object InvalidPassword : LoginValidationResult()
        object Success : LoginValidationResult()
    }

    /**
    Checks the biometric capability of the device.
    @param context The context to use for accessing the BiometricManager.
    @return An integer representing the biometric capability status:
     **/
    fun hasBiometricCapability(context: Context): Int {
        // Create a BiometricManager instance
        val biometricManager = BiometricManager.from(context)

        // Check the biometric capability and return the result
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
    }


}