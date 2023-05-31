package com.newsapp.newsapp.server

import com.newsapp.newsapp.modal.TopHeadLinesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApi {

//    @POST(AUTH_SIGN_IN)
//    suspend fun signIn(@Body loginRequest : LoginRequest) : LoginResponse

    @GET(NEW_API_TOP_HEADLINES)
    suspend fun getTopHeadLines(@Query("country") country: String = "us", @Query("category") category: String = "general", @Query("page") pageNumber: Int = 1, @Query("apiKey") apiKey: String = API_KEY) : Response<TopHeadLinesResponse>


}