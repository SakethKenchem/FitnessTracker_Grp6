package com.example.fitnesstracker_grp6.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val type: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val notes: String = "",
    val dateMillis: Long = System.currentTimeMillis()
)
