package com.newsapp.newsapp.server

import com.newsapp.newsapp.modal.TopHeadLinesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApi {

    @GET(NEW_API_TOP_HEADLINES)
    suspend fun getTopHeadLines(@Query("country") country: String = "us", @Query("category") category: String = "general", @Query("page") pageNumber: Int = 1) : Response<TopHeadLinesResponse>


}