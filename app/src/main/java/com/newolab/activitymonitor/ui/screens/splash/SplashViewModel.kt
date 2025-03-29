package com.newolab.activitymonitor.ui.screens.splash

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newolab.activitymonitor.repository.api.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    // Authentication status observed from AuthRepository
    var isLoggedIn = mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            authRepository.token.collect { token ->
                isLoggedIn.value = token != null
            }
        }
    }
}