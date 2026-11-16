package com.example.fitnesstracker_grp6.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index("usernameKey", unique = true), Index("email", unique = true)]
)
class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val usernameKey: String,
    val email: String,
    val passwordSalt: ByteArray,
    val passwordHash: ByteArray
)
