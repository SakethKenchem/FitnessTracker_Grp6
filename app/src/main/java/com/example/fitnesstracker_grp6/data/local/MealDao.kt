package com.example.fitnesstracker_grp6.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert
    suspend fun insert(meal: Meal)

    @Update
    suspend fun update(meal: Meal)

    @Delete
    suspend fun delete(meal: Meal)

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getById(id: Int): Meal?

    @Query("SELECT * FROM meals WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun getMealsBetween(start: Long, end: Long): Flow<List<Meal>>
}