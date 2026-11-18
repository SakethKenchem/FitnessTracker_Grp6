package com.example.fitnesstracker_grp6.ui.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardUiState(
    val userId: Long = 0,
    val username: String = "",
    val email: String = "",
    val waterMl: Int = 1250,
    val waterGoalMl: Int = 2500
)

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun initUser(userId: Long, username: String, email: String) {
        _uiState.value = _uiState.value.copy(
            userId = userId,
            username = username,
            email = email
        )
    }

    fun addWater(amountMl: Int = 250) {
        val current = _uiState.value.waterMl
        val updated = current + amountMl
        _uiState.value = _uiState.value.copy(waterMl = updated)
    }
}
