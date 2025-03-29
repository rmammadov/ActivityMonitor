package com.newolab.activitymonitor.model

import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: TokenData?,
    @SerializedName("message") val message: String? // For error messages
)

data class TokenData(
    @SerializedName("token") val token: String?,
)