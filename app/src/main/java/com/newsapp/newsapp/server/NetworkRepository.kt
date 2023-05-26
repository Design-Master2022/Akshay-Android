package com.newsapp.newsapp.server

import com.google.gson.JsonObject
import com.newsapp.newsapp.BuildConfig

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkRepository {

    private val responseHandler: ResponseHandler by lazy {
        ResponseHandler()
    }

    //creating a Network Interceptor to add api_key in all the request as authInterceptor
    private val interceptor = Interceptor { chain ->
        val url = chain.request().url.newBuilder().addQueryParameter("apiKey", "API_KEY").build()
        val request = chain.request()
            .newBuilder()
            .build()
        chain.proceed(request)
    }

    // we are creating a networking client using OkHttp and add our authInterceptor.
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val apiClient = OkHttpClient()
        .newBuilder()
        .connectTimeout(2, TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .addInterceptor(logging)
        .addInterceptor(OAuthInterceptor())
        .build()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(apiClient)
            .build()
    }

    private val newsApi = getRetrofit().create(NewsApi::class.java)


//    suspend fun signIn(loginRequest: LoginRequest): Response<LoginResponse> {
//        return try {
//            responseHandler.handleSuccess(newsApi.signIn(loginRequest), AppConstants.USER_LOGIN)
//        } catch (e: Exception) {
//            responseHandler.handleException(e, AppConstants.USER_LOGIN)
//        }
//    }



    private fun <T> apiCall(request: T, requestCode: Int = -1): Response<T> {
        //
        return try {
            responseHandler.handleSuccess(request, requestCode)
        } catch (e: Exception) {
            responseHandler.handleException(e, requestCode)
        }
    }


}