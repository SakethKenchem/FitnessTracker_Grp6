package com.example.fitnesstracker_grp6.data.repository

import com.example.fitnesstracker_grp6.data.local.WorkoutDao
import com.example.fitnesstracker_grp6.data.local.WorkoutSession
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val dao: WorkoutDao) {
    suspend fun addSession(session: WorkoutSession) = dao.insert(session)
    suspend fun deleteSession(session: WorkoutSession) = dao.delete(session)
    fun getSessionsForUser(userId: Long): Flow<List<WorkoutSession>> = dao.getSessionsForUser(userId)
    fun getSessionCount(userId: Long): Flow<Int> = dao.getSessionCount(userId)
    fun getTotalDuration(userId: Long): Flow<Int?> = dao.getTotalDuration(userId)
    fun getTotalCalories(userId: Long): Flow<Int?> = dao.getTotalCalories(userId)
}
