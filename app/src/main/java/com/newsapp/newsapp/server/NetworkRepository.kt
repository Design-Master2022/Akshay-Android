package com.newsapp.newsapp.server

import android.app.Application
import com.newsapp.newsapp.BuildConfig
import com.newsapp.newsapp.utils.NewsApp
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


class NetworkRepository(application: Application) {

//    private val responseHandler: ResponseHandler by lazy {
//        ResponseHandler()
//    }


    //creating a Network Interceptor to add apiKey in all the request as authInterceptor
    private val interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
        val originalHttpUrl = chain.request().url
        val url = originalHttpUrl.newBuilder().addQueryParameter("apiKey", API_KEY).build()
        request.url(url)
        chain.proceed(request.build())
    }

    // we are creating a networking client using OkHttp and add our authInterceptor.
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

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
            if ((application as NewsApp).hasNetwork() == false) {
                request = request.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached")
                    .build()
            }
            return chain.proceed(request)
        }
    }

    private val apiClient = OkHttpClient()
        .newBuilder()
        .cache((application as NewsApp).getCacheDirectory())
        .addNetworkInterceptor(REWRITE_RESPONSE_INTERCEPTOR) // only used when network is on
        .addInterceptor(REWRITE_RESPONSE_INTERCEPTOR_OFFLINE)
        .connectTimeout(2, TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .addInterceptor(logging)
        .addInterceptor(OAuthInterceptor())
        .addInterceptor(interceptor)
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


}