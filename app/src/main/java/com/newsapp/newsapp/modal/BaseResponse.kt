package com.waste.wastemanagement.modal

import com.google.gson.annotations.SerializedName

open class BaseResponse {

    @SerializedName("message")
    val message: String? = null

    @SerializedName("statusCode")
    val statusCode: Int? = null

    @SerializedName("messageCode")
    val messageCode: String? = null

    @SerializedName("status")
    val status: Int? = null

    @SerializedName("success")
    val success: Boolean? = null

}