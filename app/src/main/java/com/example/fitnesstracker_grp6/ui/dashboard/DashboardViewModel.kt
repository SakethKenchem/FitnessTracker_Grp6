package com.example.fitnesstracker_grp6.ui.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardUiState(
    val userId: Long = 0,
    val username: String = "",
    val email: String = "",
    val checkInsDone: Int = 1,
    val checkInsTotal: Int = 3
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
}
