package com.newsapp.newsapp.server

import com.newsapp.newsapp.modal.TopHeadLinesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Interface for News API endpoints.
 */
interface NewsApi {

    /**
     * Fetches top headlines from the News API.
     *
     * @param country The country code of the headlines to retrieve. Default value is "us".
     * @param category The category of the headlines to retrieve. Default value is "general".
     * @param pageNumber The page number of the headlines to retrieve. Default value is 1.
     *
     * @return A Response object containing the top headlines response.
     */
    @GET(NEW_API_TOP_HEADLINES)
    suspend fun getTopHeadLines(
        @Query("country") country: String = AppConstants.DEFAULT_COUNTRY,
        @Query("category") category: String = AppConstants.DEFAULT_CATEGORY,
        @Query("page") pageNumber: Int = 1
    ): Response<TopHeadLinesResponse>
}
