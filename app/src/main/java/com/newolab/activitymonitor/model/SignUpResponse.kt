package com.newolab.activitymonitor.model

data class SignUpResponse(
    val success: Boolean,
    val token: String, // Authentication token
)