package com.example.fitnesstracker_grp6.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, Meal::class], version = 2)
abstract class FitnessDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mealDao(): MealDao
}