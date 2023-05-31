//package com.newsapp.newsapp.server
//
//import com.newsapp.newsapp.modal.ErrorResponse
//
//
//sealed class Response <out T> {
//    data class Success<T>(val responseData: T?, val requestCode: Any): Response<T>()
//
//    data class Error<T> (val msg: String, val requestCode: Int?,val errorCode: Int? = null , val errorBody: ErrorResponse? = null): Response<T>()
//
//    data class Loading<T>(val data: T? = null): Response<T>()
//}