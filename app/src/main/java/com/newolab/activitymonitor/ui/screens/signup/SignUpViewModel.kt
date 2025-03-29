package com.newolab.activitymonitor.ui.screens.signup

import androidx.lifecycle.ViewModel
import com.newolab.activitymonitor.repository.api.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel responsible for handling me sign-in operations.
 *
 * @property authRepository The repository used to perform sign-in network requests.
 */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * Initiates the sign-in process with the provided username and password.
     *
     * @param username The me's username.
     * @param password The me's password.
     */
    fun signIn() {

    }
}