package com.newolab.activitymonitor.repository.api.equipments

import com.newolab.activitymonitor.model.EquipmentStatusRequest
import com.newolab.activitymonitor.model.EquipmentsResponse
import com.newolab.activitymonitor.model.NewEquipmentRequest
import com.newolab.activitymonitor.model.NewEquipmentResponse
import com.newolab.activitymonitor.repository.api.ApiService
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EquipmentRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun fetchEquipments(pageSize: Int, pageNum: Int): EquipmentsResponse {
        return try {
            val response = apiService.getEquipmentList(pageSize, pageNum)
            if (response.isSuccessful) {
                val responseBody = response.body()
                when {
                    responseBody?.status == "success" && responseBody.data != null -> {
                        val equipments = responseBody.data.equipment ?: emptyList()
                        val total = responseBody.data.total ?: 0
                        EquipmentsResponse.Success(equipments, total)
                    }
                    responseBody?.error != null -> {
                        EquipmentsResponse.Error(responseBody.error)
                    }
                    else -> {
                        EquipmentsResponse.Error("Unknown error occurred")
                    }
                }
            } else {
                // Handle HTTP errors
                val errorBody = response.errorBody()?.string()
                EquipmentsResponse.Error(errorBody ?: "HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            EquipmentsResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    /**
     * Updates the status of an equipment.
     *
     * @param equipmentId The ID of the equipment.
     * @param newStatus The new status to set.
     */
    suspend fun updateEquipmentStatus(equipmentId: String, newStatus: Boolean) {
        try {
            val request = EquipmentStatusRequest(id = equipmentId, status = newStatus)
            val response = apiService.updateEquipmentStatus(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.status != "success") {
                    throw Exception(body?.message ?: "Failed to update equipment status.")
                }
            } else {
                throw Exception("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: IOException) {
            throw Exception("Network Error: ${e.localizedMessage}")
        } catch (e: HttpException) {
            throw Exception("Server Error: ${e.localizedMessage}")
        } catch (e: Exception) {
            throw Exception("Error: ${e.localizedMessage}")
        }
    }

    /**
     * Adds a new equipment.
     *
     * @param name The name of the equipment.
     * @param description The description of the equipment.
     * @return EquipmentAddResponse indicating success or error.
     */
    suspend fun addEquipment(name: String, description: String): NewEquipmentResponse {
        return try {
            val request = NewEquipmentRequest(name = name, description = description)
            val response = apiService.addEquipment(request)
            if (response.isSuccessful) {
                response.body() ?: NewEquipmentResponse(
                    status = null,
                    data = null,
                    error = "Empty response body"
                )
            } else {
                // Handle HTTP errors
                val errorBody = response.errorBody()?.string()
                NewEquipmentResponse(
                    status = null,
                    data = null,
                    error = errorBody ?: "HTTP ${response.code()} ${response.message()}"
                )
            }
        } catch (e: IOException) {
            NewEquipmentResponse(
                status = null,
                data = null,
                error = "Network Error: ${e.localizedMessage}"
            )
        } catch (e: HttpException) {
            NewEquipmentResponse(
                status = null,
                data = null,
                error = "Server Error: ${e.localizedMessage}"
            )
        } catch (e: Exception) {
            NewEquipmentResponse(
                status = null,
                data = null,
                error = "Error: ${e.localizedMessage}"
            )
        }
    }
}
