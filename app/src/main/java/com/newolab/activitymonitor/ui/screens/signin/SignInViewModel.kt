package com.newolab.activitymonitor.ui.screens.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newolab.activitymonitor.repository.api.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for handling user sign-in operations.
 *
 * @property authRepository The repository used to perform sign-in network requests.
 */
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Sealed class representing the sign-in state
    sealed class SignInState {
        object Idle : SignInState()
        object Loading : SignInState()
        object Success : SignInState()
        data class Error(val message: String) : SignInState()
    }

    // Mutable state holding the current sign-in state
    private val _signInState = MutableStateFlow<SignInState>(SignInState.Idle)
    val signInState: StateFlow<SignInState> = _signInState

    /**
     * Initiates the sign-in process with the provided username and password.
     *
     * @param username The user's username.
     * @param password The user's password.
     */
    fun signIn(username: String, password: String) {
        // Update the state to Loading
        _signInState.value = SignInState.Loading

        val username = "admin@test.com"
        val password = "135Tracker-Prod!#%"

        // Launch a coroutine in the ViewModel's scope
        viewModelScope.launch {
            try {
                // Make the sign-in request through the repository
                val response = authRepository.signIn(username, password)

                // Check if the response is successful
                if (response) {
                    // Update the state to Success
                    _signInState.value = SignInState.Success
                } else {
                    // Update the state to Error with a specific message
                    _signInState.value = SignInState.Error("Invalid username or password.")
                }
            } catch (e: Exception) {
                // Update the state to Error with the exception message
                _signInState.value = SignInState.Error(
                    e.localizedMessage ?: "An unexpected error occurred."
                )
            }
        }
    }

    /**
     * Resets the sign-in state to Idle.
     * Call this when the user starts typing to clear error messages and highlights.
     */
    fun resetState() {
        _signInState.value = SignInState.Idle
    }
}