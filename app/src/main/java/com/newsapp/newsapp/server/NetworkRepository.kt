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


class NetworkRepository(private val application: Application) {

    // Interceptor to add apiKey to all requests
    private val interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
        val originalHttpUrl = chain.request().url
        val url = originalHttpUrl.newBuilder().addQueryParameter("apiKey", API_KEY).build()
        request.url(url)
        chain.proceed(request.build())
    }

    // Logging interceptor for debugging network requests
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor to handle cache control in responses
    private val cacheControlInterceptor: Interceptor = object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalResponse: Response = chain.proceed(chain.request())
            val cacheControl = originalResponse.header(AppConstants.HEADER_CACHE_CONTROL)
            return if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains(
                    "cache"
                ) ||
                cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")
            ) {
                originalResponse.newBuilder()
                    .removeHeader(AppConstants.HEADER_PRAGMA)
                    .header(AppConstants.HEADER_CACHE_CONTROL, "public, max-age=" + 5000)
                    .build()
            } else {
                originalResponse
            }
        }
    }

    // Interceptor to handle cache control when offline
    private val offlineCacheControlInterceptor: Interceptor = object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request: Request = chain.request()
            if (!(application as NewsApp).hasNetwork()) {
                request = request.newBuilder()
                    .removeHeader(AppConstants.HEADER_PRAGMA)
                    .header(AppConstants.HEADER_CACHE_CONTROL, "public, only-if-cached")
                    .build()
            }
            return chain.proceed(request)
        }
    }

    // OkHttpClient instance for network operations
    private val apiClient = OkHttpClient.Builder()
        .cache((application as NewsApp).getCacheDirectory())
        .addInterceptor(loggingInterceptor)
        .addInterceptor(OAuthInterceptor())
        .addInterceptor(interceptor)
        .addNetworkInterceptor(cacheControlInterceptor)
        .addInterceptor(offlineCacheControlInterceptor)
        .connectTimeout(2, TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .build()

    // Retrofit instance for API calls
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(apiClient)
        .build()

    // API interface for top headlines
    private val newsApi = retrofit.create(NewsApi::class.java)

    /**
     * Get top headlines from the News API.
     *
     * @param country    The country for the headlines.
     * @param category   The category for the headlines.
     * @param pageNumber The page number for pagination.
     * @return The response containing the top headlines.
     */
    suspend fun getTopHeadLines(country: String, category: String, pageNumber: Int) =
        newsApi.getTopHeadLines(country, category, pageNumber)
}