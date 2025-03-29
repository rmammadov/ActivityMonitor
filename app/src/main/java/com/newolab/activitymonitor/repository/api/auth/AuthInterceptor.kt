package com.newolab.activitymonitor.repository.api.auth

import com.newolab.activitymonitor.repository.datastore.UserPreferences
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Retrieve the token synchronously
        val token = userPreferences.getTokenSynchronously()

        // If token is present, add it to the header
        val modifiedRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .addHeader("x-api-auth", token)
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(modifiedRequest)
    }
}