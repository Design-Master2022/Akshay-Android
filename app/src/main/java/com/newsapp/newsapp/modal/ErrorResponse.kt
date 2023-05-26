package com.newsapp.newsapp.modal

import com.google.gson.annotations.SerializedName

data class ErrorResponse(@field:SerializedName("statusCode") val status: Int,
                         @field:SerializedName("message") val message: String,
                         @field:SerializedName("messageCode") val messageCode: String,
                         @field:SerializedName("success") val success: Boolean,
                         @field:SerializedName("timestamp") val timeStamp: Long)