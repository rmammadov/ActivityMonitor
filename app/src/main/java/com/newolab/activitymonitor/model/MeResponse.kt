package com.newolab.activitymonitor.model

import com.google.gson.annotations.SerializedName

// Sealed class to represent API responses
sealed class MeResponse {
    data class Success(val me: Me) : MeResponse()
    data class Error(val errorMessage: String) : MeResponse()
}

// Raw response from the API
data class MeResponseRaw(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: MeData?,
    @SerializedName("error") val error: String?
)

// Data class representing the "data" field in the JSON
data class MeData(
    @SerializedName("user") val me: Me
)

// Data class representing the user details
data class Me(
    @SerializedName("_id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("registration") val registration: String,
    @SerializedName("isAdmin") val isAdmin: Boolean,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("position") val position: String?,
    @SerializedName("factory") val factory: String?
)
