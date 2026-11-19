package com.example.fitnesstracker_grp6.data.setup

import android.app.Application
import androidx.room.Room
import com.example.fitnesstracker_grp6.data.local.FitnessDatabase
import com.example.fitnesstracker_grp6.data.repository.AccountRepository
import com.example.fitnesstracker_grp6.data.local.MealRepository

class FitnessTrackerApplication : Application() {
    private val database by lazy {
        Room.databaseBuilder(this, FitnessDatabase::class.java, "fitness.db")
            .fallbackToDestructiveMigration(true)
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .build()
    }

    val accountRepository by lazy { AccountRepository(database.userDao()) }
    val mealRepository by lazy { MealRepository(database.mealDao()) }
}
