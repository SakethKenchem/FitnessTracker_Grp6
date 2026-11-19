package com.example.fitnesstracker_grp6.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker_grp6.data.local.WorkoutSession
import com.example.fitnesstracker_grp6.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    fun getSessions(userId: Long): Flow<List<WorkoutSession>> =
        repository.getSessionsForUser(userId)

    fun getSessionCount(userId: Long): Flow<Int> =
        repository.getSessionCount(userId)

    fun getTotalDuration(userId: Long): Flow<Int?> =
        repository.getTotalDuration(userId)

    fun getTotalCalories(userId: Long): Flow<Int?> =
        repository.getTotalCalories(userId)

    fun addWorkout(
        userId: Long,
        type: String,
        durationMinutes: Int,
        caloriesBurned: Int,
        notes: String
    ) {
        viewModelScope.launch {
            val session = WorkoutSession(
                userId = userId,
                type = type.ifBlank { "General Workout" },
                durationMinutes = durationMinutes,
                caloriesBurned = caloriesBurned,
                notes = notes
            )
            repository.addSession(session)
        }
    }

    fun deleteWorkout(session: WorkoutSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }
}

class WorkoutViewModelFactory(private val repository: WorkoutRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
