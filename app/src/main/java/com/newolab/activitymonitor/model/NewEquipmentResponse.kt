package com.newolab.activitymonitor.model

data class NewEquipmentResponse(
    val status: String?,
    val data: EquipmentData?,
    val error: String?
)

data class EquipmentData(
    val id: String
)