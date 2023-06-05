package com.newsapp.newsapp.server

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.newsapp.newsapp.BuildConfig
import com.newsapp.newsapp.utils.NewsApp
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.security.AccessController.getContext
import java.util.concurrent.TimeUnit


object NetworkRepository {

//    private val responseHandler: ResponseHandler by lazy {
//        ResponseHandler()
//    }

    var application: NewsApp ?= null // Add this


//    var SIZE_OF_CACHE = (10 * 1024 * 1024 // 10 MiB
//            ).toLong()
//    var cache = Cache(File(application?.applicationContext?.cacheDir, "http"), SIZE_OF_CACHE)

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

//    private fun cache(): Cache? {
////        val cacheSize = 5 * 1024 * 1024.toLong()
////        return application?.applicationContext?.cacheDir?.let { Cache(it, cacheSize) }
//
//        var cache: Cache? = null
//        try {
//            cache = Cache(
//                File(application?.applicationContext?.cacheDir, "http-cache"),
//                10 * 1024 * 1024
//            ) // 10 MB
//        } catch (e: Exception) {
//            Log.e("TAG", ""+e+"Could not create Cache!")
//        }
//        return cache
//    }
//
//    class NetworkInterceptor: Interceptor {
//        override fun intercept(chain: Interceptor.Chain): Response {
//            Log.d("Tag","network interceptor: called.")
//
//            val response = chain.proceed(chain.request())
//
//            val cacheControl = CacheControl.Builder()
//                .maxAge(5, TimeUnit.SECONDS)
//                .build()
//
//            return response.newBuilder()
//                .removeHeader(HEADER_PRAGMA)
//                .removeHeader(HEADER_CACHE_CONTROL)
//                .header(HEADER_CACHE_CONTROL, cacheControl.toString())
//                .build()
//        }
//    }
//
//    @MustBeDocumented
//    @Target(AnnotationTarget.FUNCTION)
//    @Retention(AnnotationRetention.RUNTIME)
//    annotation class Cacheable {}
//
//    open class OfflineCacheInterceptor : Interceptor {
//        override fun intercept(chain: Interceptor.Chain): Response {
//            var request = chain.request()
//            val invocation: Invocation? = request.tag(Invocation::class.java)
//
//            if (invocation != null) {
//                val annotation: Cacheable? =
//                    invocation.method().getAnnotation(Cacheable::class.java)
//
//                /* check if this request has the [Cacheable] annotation */
//                if (annotation != null &&
//                    annotation.annotationClass.simpleName.equals("Cacheable") &&
//                    hasNetwork() == false
//                ) {
////                    Log.d("CACHE ANNOTATION: called.::%s", annotation.annotationClass.simpleName)
//
//                    // prevent caching when network is on. For that we use the "networkInterceptor"
//                    Log.d("TAG","cache interceptor: called.")
//                    val cacheControl = CacheControl.Builder()
//                        .maxStale(7, TimeUnit.DAYS)
//                        .build()
//
//                    request = request.newBuilder()
//                        .removeHeader(HEADER_PRAGMA)
//                        .removeHeader(HEADER_CACHE_CONTROL)
//                        .cacheControl(cacheControl)
//                        .build()
//                } else {
//                    Log.d("TAG", "cache interceptor: not called.")
//                }
//            }
//            return chain.proceed(request)
//        }
//    }

//    private var onlineInterceptor: Interceptor = object : Interceptor {
//        @Throws(IOException::class)
//        override fun intercept(chain: Interceptor.Chain): Response {
//            val response: Response = chain.proceed(chain.request())
//            val maxAge = 60 // read from cache for 60 seconds even if there is internet connection
//            return response.newBuilder()
//                .header("Cache-Control", "public, max-age=$maxAge")
//                .removeHeader("Pragma")
//                .build()
//        }
//    }
//
//    private var offlineInterceptor: Interceptor = object : Interceptor {
//        @Throws(IOException::class)
//        override fun intercept(chain: Interceptor.Chain): Response {
//            var request: Request = chain.request()
//            if (hasNetwork() == false) {
//                val maxStale = 60 * 60 * 24 * 30 // Offline cache available for 30 days
//                request = request.newBuilder()
//                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
//                    .removeHeader("Pragma")
//                    .build()
//            }
//            return chain.proceed(request)
//        }
//    }

    private val REWRITE_RESPONSE_INTERCEPTOR: Interceptor = object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalResponse: Response = chain.proceed(chain.request())
            val cacheControl = originalResponse.header("Cache-Control")
            return if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains(
                    "cache"
                ) ||
                cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")
            ) {
                originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + 5000)
                    .build()
            } else {
                originalResponse
            }
        }
    }

    private val REWRITE_RESPONSE_INTERCEPTOR_OFFLINE: Interceptor = object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request: Request = chain.request()
            if (application?.hasNetwork() == false) {
                request = request.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached")
                    .build()
            }
            return chain.proceed(request)
        }
    }

//    var cacheSize = 10 * 1024 * 1024 // 10 MB
//
//    var cache = this.cacheDir?.let { Cache(it, cacheSize.toLong()) }

    private val apiClient = OkHttpClient()
        .newBuilder()
//        .cache(cache)
//        .addNetworkInterceptor(onlineInterceptor) // only used when network is on
//        .addInterceptor(offlineInterceptor)
        .addNetworkInterceptor(REWRITE_RESPONSE_INTERCEPTOR)
        .addInterceptor(REWRITE_RESPONSE_INTERCEPTOR_OFFLINE)
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