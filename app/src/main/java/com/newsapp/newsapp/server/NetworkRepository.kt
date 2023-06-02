package com.newsapp.newsapp.server

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.newsapp.newsapp.BuildConfig
import okhttp3.*
import okhttp3.internal.cache.CacheInterceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


object NetworkRepository {

//    private val responseHandler: ResponseHandler by lazy {
//        ResponseHandler()
//    }
    var application: Application ?= null // Add this

//    const val cacheSize = (5 * 1024 * 1024).toLong()
//    private val myCache = application?.cacheDir?.let { Cache(it, cacheSize) }

//    var httpCacheDirectory: File = File(application?.cacheDir, "http-cache")
//    var cacheSize = 10 * 1024 * 1024 // 10 MiB
//    var cache = Cache(httpCacheDirectory, cacheSize.toLong())


    var SIZE_OF_CACHE = (10 * 1024 * 1024 // 10 MiB
            ).toLong()
    var cache = Cache(File(application?.getCacheDir(), "http"), SIZE_OF_CACHE)

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

    class CachingControlInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request: Request = chain.request()

            // Add Cache Control only for GET methods
            if (request.method.equals("GET")) {
                request = if (application?.let { hasNetwork(it) } == true) {
                    // 1 day
                    request.newBuilder()
                        .header("Cache-Control", "only-if-cached")
                        .build()
                } else {
                    // 4 weeks stale
                    request.newBuilder()
                        .header("Cache-Control", "public, max-stale=2419200")
                        .build()
                }
            }
            val originalResponse: Response = chain.proceed(request)
            return originalResponse.newBuilder()
                .header("Cache-Control", "max-age=600")
                .build()
        }
    }

    private val apiClient = OkHttpClient()
        .newBuilder()
        .cache(cache)
        .addNetworkInterceptor(CachingControlInterceptor())
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


    private fun hasNetwork(context: Context): Boolean? {
        var isConnected: Boolean? = false // Initial Value
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }

    suspend fun getTopHeadLines(country: String, category: String, pageNumber: Int) =
        newsApi.getTopHeadLines(country, category, pageNumber)

//    suspend fun signIn(loginRequest: LoginRequest): Response<LoginResponse> {
//        return try {
//            responseHandler.handleSuccess(newsApi.signIn(loginRequest), AppConstants.USER_LOGIN)
//        } catch (e: Exception) {
//            responseHandler.handleException(e, AppConstants.USER_LOGIN)
//        }
//    }

//    private fun <T> apiCall(request: T, requestCode: Int = -1): Response<T> {
//        //
//        return try {
//            responseHandler.handleSuccess(request, requestCode)
//        } catch (e: Exception) {
//            responseHandler.handleException(e, requestCode)
//        }
//    }


}