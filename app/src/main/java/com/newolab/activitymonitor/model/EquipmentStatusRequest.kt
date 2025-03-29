package com.newolab.activitymonitor.model

import com.google.gson.annotations.SerializedName

data class EquipmentStatusRequest(
    @SerializedName("id") val id: String,
    @SerializedName("status") val status: Boolean
)