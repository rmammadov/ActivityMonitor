package com.newolab.activitymonitor.ui.screens.signin

import com.newolab.activitymonitor.model.SignInResponse

/**
 * Represents the different states of the sign-in process.
 */
sealed class SignInState {
    object Idle : SignInState()
    object Loading : SignInState()
    data class Success(val signInResponse: Boolean) : SignInState()
    data class Error(val message: String) : SignInState()
}