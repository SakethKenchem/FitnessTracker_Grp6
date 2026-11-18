package com.example.fitnesstracker_grp6.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class FitnessDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
