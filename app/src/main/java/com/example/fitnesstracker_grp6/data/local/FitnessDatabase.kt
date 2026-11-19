package com.example.fitnesstracker_grp6.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class, Meal::class, WorkoutSession::class], version = 3)
abstract class FitnessDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mealDao(): MealDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        val MIGRATION_1_3 = addTrackingTables(1)
        val MIGRATION_2_3 = addTrackingTables(2)

        // Version 2 was used by both the meal-only and workout-only branches.
        private fun addTrackingTables(startVersion: Int): Migration =
            object : Migration(startVersion, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS meals (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            name TEXT NOT NULL,
                            mealType TEXT NOT NULL,
                            calories INTEGER NOT NULL,
                            protein REAL NOT NULL,
                            carbs REAL NOT NULL,
                            fat REAL NOT NULL,
                            timestamp INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS workout_sessions (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            userId INTEGER NOT NULL,
                            type TEXT NOT NULL,
                            durationMinutes INTEGER NOT NULL,
                            caloriesBurned INTEGER NOT NULL,
                            notes TEXT NOT NULL,
                            dateMillis INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )
                }
            }
    }
}
