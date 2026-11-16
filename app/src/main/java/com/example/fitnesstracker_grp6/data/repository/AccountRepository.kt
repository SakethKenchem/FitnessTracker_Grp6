package com.example.fitnesstracker_grp6.data.repository

import com.example.fitnesstracker_grp6.data.local.User
import com.example.fitnesstracker_grp6.data.local.UserDao
import com.example.fitnesstracker_grp6.data.model.Account
import com.example.fitnesstracker_grp6.data.security.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class AccountRepository(private val userDao: UserDao) {
    suspend fun register(username: String, email: String, password: String): Boolean {
        val passwordSalt = PasswordHasher.newSalt()
        val passwordHash = withContext(Dispatchers.Default) {
            PasswordHasher.hash(password, passwordSalt)
        }
        val user = User(
            username = username,
            usernameKey = username.lowercase(Locale.ROOT),
            email = email.lowercase(Locale.ROOT),
            passwordSalt = passwordSalt,
            passwordHash = passwordHash
        )
        return userDao.insert(user) != -1L
    }

    suspend fun signIn(usernameOrEmail: String, password: String): Account? {
        val user = userDao.findByUsernameOrEmail(usernameOrEmail.lowercase(Locale.ROOT))
            ?: return null
        val passwordMatches = withContext(Dispatchers.Default) {
            PasswordHasher.matches(password, user.passwordSalt, user.passwordHash)
        }
        if (!passwordMatches) return null
        return Account(user.id, user.username, user.email)
    }
}
