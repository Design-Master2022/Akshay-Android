package com.newsapp.newsapp.server

import okhttp3.Interceptor

class OAuthInterceptor : Interceptor {

    private val tokenType: String = "Bearer"
    private var accessToken: String? = null

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
        request.apply {
            addHeader("Content-Type", "application/json")
            addHeader("type", "Surveyor")
        }
//        accessToken = CommonSharedPreferences.readString(CommonSharedPreferences.ACCESS_TOKEN)
//        if (accessToken != null && accessToken?.isNotEmpty() == true) {
//            request.header("Authorization", "$tokenType $accessToken")
//        }

        return chain.proceed(request.build())
    }
}