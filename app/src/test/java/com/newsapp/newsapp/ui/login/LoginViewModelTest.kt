package com.newsapp.newsapp.ui.login

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginViewModelTest {

        private lateinit var loginViewModel: LoginViewModel

        @Before
        fun setup() {

            // Get the Application context
            val context = ApplicationProvider.getApplicationContext<Context>()

            // Cast the context to an Application instance
            val application = context as Application

            // Instantiate the ViewModel with the Application instance
            loginViewModel = LoginViewModel(application)
        }

    @Test
    fun `empty username`() {
        val result = loginViewModel.validateLogin("", "Test@123")
        Truth.assertThat(result)
    }

    @Test
    fun `invalid username`() {
        val result = loginViewModel.validateLogin("xyz", "Test@123")
        Truth.assertThat(result)
    }

    @Test
    fun `empty password`() {
        val result = loginViewModel.validateLogin("a@b.com", "")
        Truth.assertThat(result)
    }

    @Test
    fun `invalid password length less than 6`() {
        val result = loginViewModel.validateLogin("a@b.com", "Test")
        Truth.assertThat(result)
    }

    @Test
    fun `valid username and password`() {
        val result = loginViewModel.validateLogin("a@b.com", "Test@123")
        Truth.assertThat(result)
    }
}