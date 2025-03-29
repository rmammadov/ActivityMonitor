package com.newolab.activitymonitor.repository.api.auth

import com.newolab.activitymonitor.model.MeResponse
import com.newolab.activitymonitor.model.MeResponseRaw
import com.newolab.activitymonitor.model.SignInRequest
import com.newolab.activitymonitor.model.SignUpRequest
import com.newolab.activitymonitor.model.Me
import com.newolab.activitymonitor.repository.api.ApiService
import com.newolab.activitymonitor.repository.datastore.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    private val authScope = CoroutineScope(Dispatchers.IO + Job())

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val _currentMe = MutableStateFlow<Me?>(null)
    val currentMe: StateFlow<Me?> = _currentMe

    private val _meResponse = MutableStateFlow<MeResponse?>(null)
    val meResponse: StateFlow<MeResponse?> = _meResponse

    init {
        // Initialize authentication state from DataStore
        authScope.launch {
            userPreferences.token.collect { savedToken ->
                _token.value = savedToken
//                _isLoggedIn.value = savedToken != null
            }
        }
    }

    suspend fun signUp(email: String, password: String): Boolean {
        return try {
            val response = apiService.signUp(SignUpRequest(email, password))
            if (response.success) {
                // Optionally, automatically log in the me after sign-up
//                _isLoggedIn.value = true
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun signIn(email: String, password: String): Boolean {
        return try {
            val response = apiService.signIn(
                signInRequest = SignInRequest(email, password)
            )
            if (response.status == "success" && response.data?.token != null) {
                userPreferences.saveToken(response.data.token)
                _token.value = response.data.token
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun fetchCurrentUser() {
        // Retrieve the token from UserPreferences
        val token = userPreferences.getTokenSynchronously()
        if (token == null) {
            _meResponse.value = MeResponse.Error("Authentication token is missing.")
            // Optionally, handle token invalidation
            invalidateToken()
            return
        }

        try {
            val response: Response<MeResponseRaw> = apiService.getMe()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.status == "success" && body.data != null) {
                    _currentMe.value = body.data.me
                    _meResponse.value = MeResponse.Success(body.data.me)
                } else if (body?.error != null) {
                    _meResponse.value = MeResponse.Error(body.error)
                    // Optionally, handle token invalidation
                    invalidateToken()
                } else {
                    _meResponse.value = MeResponse.Error("Unknown error occurred.")
                }
            } else {
                // Handle non-2xx HTTP responses
                val errorBody = response.errorBody()?.string()
                _meResponse.value = MeResponse.Error(errorBody ?: "Unknown server error.")
                // Optionally, handle token invalidation
                invalidateToken()
            }
        } catch (e: IOException) {
            // Network or conversion error
            e.printStackTrace()
            _meResponse.value = MeResponse.Error("Network error. Please try again.")
        } catch (e: HttpException) {
            // HTTP protocol error
            e.printStackTrace()
            _meResponse.value = MeResponse.Error("Server error. Please try again.")
        } catch (e: Exception) {
            // Other unexpected errors
            e.printStackTrace()
            _meResponse.value = MeResponse.Error("An unexpected error occurred.")
        }
    }

    private suspend fun invalidateToken() {
        _token.value = null
        _currentMe.value = null
        userPreferences.clearToken()
    }

    suspend fun logOut() {
        _token.value = null
        _currentMe.value = null
        _meResponse.value = null
        userPreferences.clearToken()
    }
}

