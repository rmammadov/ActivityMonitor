package com.newolab.activitymonitor.model

import com.google.gson.annotations.SerializedName

sealed class EquipmentsResponse {
    data class Success(val equipmentList: List<Equipment>, val total: Int) : EquipmentsResponse()
    data class Error(val errorMessage: String) : EquipmentsResponse()
}

data class EquipmentsResponseRaw(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: EquipmentsData?,
    @SerializedName("error") val error: String?
)

data class EquipmentsData(
    @SerializedName("total") val total: Int?,
    @SerializedName("devices") val equipment: List<Equipment>? // Updated to map "devices"
)

data class Equipment(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("owner") val owner: String?,
    @SerializedName("status") val status: Boolean,
    @SerializedName("created") val created: String?,
    @SerializedName("updated") val updated: String?,
    @SerializedName("utilization") val utilization: Double?, // Changed from Int? to Double?
    @SerializedName("__v") val v: Int?,
    @SerializedName("model") val model: EquipmentModel?
)

data class EquipmentModel(
    @SerializedName("name") val name: String?,
    @SerializedName("maintenancePeriod") val maintenancePeriod: Int?
)
