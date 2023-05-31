//package com.newsapp.newsapp.server
//
//import com.google.gson.Gson
//import com.newsapp.newsapp.modal.ErrorResponse
//import retrofit2.HttpException
//import java.net.ConnectException
//import java.net.SocketTimeoutException
//
//
//open class ResponseHandler {
//
//    fun <T> handleSuccess(data: T, code: Any): Response<T> {
//        return Response.Success(data, code)
//    }
//
//    fun <T> handleException(e: Exception, code: Int = 0): Response<T> {
//        return when (e) {
//            is HttpException -> {
//                Response.Error(
//                    getErrorMessage(e.code()),
//                    code,
//                    e.code(),
//                    errorBody = getErrorBody(e)
//                )
//            }
//            is ConnectException -> Response.Error(
//                getErrorMessage(1),
//                code
//            )
//            is SocketTimeoutException -> Response.Error(
//                getErrorMessage(0),
//                code
//            )
//            else -> Response.Error(
//                getErrorMessage(Int.MAX_VALUE),
//                code
//            )
//        }
//    }
//
//    private fun getErrorBody(e: HttpException): ErrorResponse? {
//        try {
//            return Gson().fromJson(
//                e.response()?.errorBody()?.byteStream()?.let {
//                    String(it.readBytes())
//                },
//                ErrorResponse::class.java
//            )
//        } catch (e: Exception) { }
//        return null
//    }
//
//    private fun getErrorMessage(code: Int): String {
//        return when (code) {
//            TIME_OUT -> TIME_OUT_MSG
//            UN_AUTHORIZED -> UN_AUTHORIZED_MSG
//            NOT_FOUND -> NOT_FOUND_MSG
//            CONNECTION -> CONNECTION_MSG
//            INTERNAL_SERVER_ERROR -> INTERNAL_SERVER_ERROR_MSG
//            else -> ERROR_MSG
//        }
//    }
//}
