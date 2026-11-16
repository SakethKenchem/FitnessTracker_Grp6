package com.example.fitnesstracker_grp6.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE usernameKey = :usernameOrEmail OR email = :usernameOrEmail LIMIT 1")
    suspend fun findByUsernameOrEmail(usernameOrEmail: String): User?
}
