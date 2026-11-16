package com.example.fitnesstracker_grp6.data.setup

import android.app.Application
import androidx.room.Room
import com.example.fitnesstracker_grp6.data.local.FitnessDatabase
import com.example.fitnesstracker_grp6.data.repository.AccountRepository

class FitnessTrackerApplication : Application() {
    private val database by lazy {
        Room.databaseBuilder(this, FitnessDatabase::class.java, "fitness.db").build()
    }

    val accountRepository by lazy { AccountRepository(database.userDao()) }
}
