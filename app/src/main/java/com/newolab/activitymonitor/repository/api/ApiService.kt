package com.newolab.activitymonitor.repository.api

import com.newolab.activitymonitor.model.EquipmentStatusRequest
import com.newolab.activitymonitor.model.EquipmentStatusResponse
import com.newolab.activitymonitor.model.EquipmentsResponseRaw
import com.newolab.activitymonitor.model.MeResponseRaw
import com.newolab.activitymonitor.model.NewEquipmentRequest
import com.newolab.activitymonitor.model.NewEquipmentResponse
import com.newolab.activitymonitor.model.SignInRequest
import com.newolab.activitymonitor.model.SignInResponse
import com.newolab.activitymonitor.model.SignUpRequest
import com.newolab.activitymonitor.model.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("auth/signup")
    suspend fun signUp(
        @Body signUpRequest: SignUpRequest
    ): SignUpResponse

    @POST("auth/signin")
    suspend fun signIn(
        @Body signInRequest: SignInRequest
    ): SignInResponse

    @GET("auth/me")
    suspend fun getMe(
    ): Response<MeResponseRaw>

    @GET("devices/list")
    suspend fun getEquipmentList(
        @Query("pageSize") pageSize: Int,
        @Query("pageNum") pageNum: Int
    ): Response<EquipmentsResponseRaw>

    @POST("devices/update")
    suspend fun updateEquipmentStatus(
        @Body request: EquipmentStatusRequest,
    ): Response<EquipmentStatusResponse>

    @POST("devices/add")
    suspend fun addEquipment(
        @Body equipmentRequest: NewEquipmentRequest
    ): Response<NewEquipmentResponse>
}