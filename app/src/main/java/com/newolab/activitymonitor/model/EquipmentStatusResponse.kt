package com.newolab.activitymonitor.model

import com.google.gson.annotations.SerializedName

data class EquipmentStatusResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String? // Present only in error responses
)