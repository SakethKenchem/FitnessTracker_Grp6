package com.example.fitnesstracker_grp6.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: WorkoutSession): Long

    @Delete
    suspend fun delete(session: WorkoutSession)

    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY dateMillis DESC")
    fun getSessionsForUser(userId: Long): Flow<List<WorkoutSession>>

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE userId = :userId")
    fun getSessionCount(userId: Long): Flow<Int>

    @Query("SELECT SUM(durationMinutes) FROM workout_sessions WHERE userId = :userId")
    fun getTotalDuration(userId: Long): Flow<Int?>

    @Query("SELECT SUM(caloriesBurned) FROM workout_sessions WHERE userId = :userId")
    fun getTotalCalories(userId: Long): Flow<Int?>
}
