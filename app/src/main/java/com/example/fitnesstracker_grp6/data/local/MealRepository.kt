package com.example.fitnesstracker_grp6.data.local

import kotlinx.coroutines.flow.Flow

class MealRepository(private val mealDao: MealDao) {

    fun mealsBetween(start: Long, end: Long): Flow<List<Meal>> =
        mealDao.getMealsBetween(start, end)

    suspend fun getMeal(id: Int): Meal? = mealDao.getById(id)

    suspend fun addMeal(meal: Meal) = mealDao.insert(meal)

    suspend fun updateMeal(meal: Meal) = mealDao.update(meal)

    suspend fun deleteMeal(meal: Meal) = mealDao.delete(meal)
}