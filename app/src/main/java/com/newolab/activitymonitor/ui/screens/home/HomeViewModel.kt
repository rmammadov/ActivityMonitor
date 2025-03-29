package com.newolab.activitymonitor.ui.screens.home

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.newolab.activitymonitor.model.EquipmentsResponse
import com.newolab.activitymonitor.model.Me
import com.newolab.activitymonitor.model.Equipment
import com.newolab.activitymonitor.repository.api.auth.AuthRepository
import com.newolab.activitymonitor.repository.api.equipments.EquipmentRepository
import com.newolab.activitymonitor.repository.datastore.UserPreferences
import com.newolab.activitymonitor.ui.components.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val equipmentRepository: EquipmentRepository,
    private val userPreferences: UserPreferences // Inject user preferences
) : ViewModel() {

    // Me State
    val currentMe: StateFlow<Me?> = authRepository.currentMe

    // Language State
    private val _currentLanguage = MutableStateFlow("az")
    val currentLanguage: StateFlow<String> = _currentLanguage

    // Equipment List State
    private val _equipmentsResponse = MutableStateFlow<EquipmentsResponse?>(null)
    val equipmentsResponse: StateFlow<EquipmentsResponse?> = _equipmentsResponse

    // Error Messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Current Page Number
    private var currentPageNum = 1

    // Page Size
    private val pageSize = 50

    // Status Update State
    sealed class UpdateStatusState {
        object Idle : UpdateStatusState()
        object Loading : UpdateStatusState()
        data class Success(val message: String) : UpdateStatusState()
        data class Error(val message: String?) : UpdateStatusState()
    }

    private val _updateStatusState = MutableStateFlow<UpdateStatusState>(UpdateStatusState.Idle)
    val updateStatusState: StateFlow<UpdateStatusState> = _updateStatusState

    // Add Equipment State
    sealed class AddEquipmentState {
        object Idle : AddEquipmentState()
        object Loading : AddEquipmentState()
        data class Success(val id: String) : AddEquipmentState()
        data class Error(val message: String?) : AddEquipmentState()
    }

    private val _addEquipmentState = MutableStateFlow<AddEquipmentState>(AddEquipmentState.Idle)
    val addEquipmentState: StateFlow<AddEquipmentState> = _addEquipmentState

    init {
        observeLanguage()
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            userPreferences.language.collect {
                _currentLanguage.value = it
            }
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val newLang = if (_currentLanguage.value == "az") "en" else "az"
            userPreferences.saveLanguage(newLang)
        }
    }

    fun fetchEquipments() {
        viewModelScope.launch {
            try {
                val response = equipmentRepository.fetchEquipments(pageSize, currentPageNum)
                when (response) {
                    is EquipmentsResponse.Success -> {
                        _equipmentsResponse.value = response
                        Log.d("HomeViewModel", "Fetched equipments: ${response.equipmentList.size}")
                    }
                    is EquipmentsResponse.Error -> {
                        _equipmentsResponse.value = null
                        _errorMessage.value = response.errorMessage
                        Log.e("HomeViewModel", "Error fetching equipments: ${response.errorMessage}")
                    }
                }
            } catch (e: Exception) {
                _equipmentsResponse.value = null
                _errorMessage.value = e.message ?: "An unexpected error occurred."
                Log.e("HomeViewModel", "Exception fetching equipments", e)
            }
        }
    }

    // Function to fetch current user details
    fun fetchCurrentUser() {
        viewModelScope.launch {
            try {
                authRepository.fetchCurrentUser()
            } catch (e: Exception) {
                // Handle exceptions if necessary
            }
        }
    }

    // Sign out function
    fun signOut(navController: NavController) {
        viewModelScope.launch {
            authRepository.logOut()
            navController.navigate(Routes.SIGN_IN) {
                popUpTo(Routes.HOME) { inclusive = true }
            }
        }
    }

    fun getEquipmentById(equipmentId: String?): Flow<Equipment?> {
        return _equipmentsResponse.map { response ->
            when (response) {
                is EquipmentsResponse.Success -> {
                    response.equipmentList.find { it.id == equipmentId }
                }
                else -> null
            }
        }
    }

    fun updateEquipmentStatus(equipmentId: String, newStatus: Boolean) {
        viewModelScope.launch {
            _updateStatusState.value = UpdateStatusState.Loading
            try {
                equipmentRepository.updateEquipmentStatus(equipmentId, newStatus)
                _updateStatusState.value = UpdateStatusState.Success("Status updated successfully.")
                // Refresh the equipments list to reflect changes
                fetchEquipments()
            } catch (e: Exception) {
                _updateStatusState.value = UpdateStatusState.Error(e.message ?: "Failed to update status.")
            }
        }
    }

    fun addEquipment(name: String, description: String) {
        viewModelScope.launch {
            _addEquipmentState.value = AddEquipmentState.Loading
            try {
                val response = equipmentRepository.addEquipment(name, description)
                if (response.status == "success" && response.data != null) {
                    _addEquipmentState.value = AddEquipmentState.Success(response.data.id)
                    // Optionally, refresh the equipment list
                    fetchEquipments()
                } else if (response.error != null) {
                    _addEquipmentState.value = AddEquipmentState.Error(response.error)
                } else {
                    _addEquipmentState.value = AddEquipmentState.Error("Unknown error occurred.")
                }
            } catch (e: Exception) {
                _addEquipmentState.value = AddEquipmentState.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }

    fun resetAddEquipmentState() {
        _addEquipmentState.value = AddEquipmentState.Idle
    }

    fun saveLanguage(lang: String) {
        viewModelScope.launch {
            userPreferences.saveLanguage(lang)
        }
    }

    // Utility function to update the Locale of the current context
    fun updateLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
