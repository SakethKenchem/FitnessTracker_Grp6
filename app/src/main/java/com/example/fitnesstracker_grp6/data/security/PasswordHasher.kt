package com.example.fitnesstracker_grp6.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

internal object PasswordHasher {
    private val secureRandom = SecureRandom()

    fun newSalt(): ByteArray = ByteArray(16).also(secureRandom::nextBytes)

    fun hash(password: String, salt: ByteArray): ByteArray {
        // PBKDF2WithHmacSHA1 is available across the app's supported Android versions.
        val passwordChars = password.toCharArray()
        val keySpec = PBEKeySpec(passwordChars, salt, 1_300_000, 160)
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(keySpec).encoded
        } finally {
            keySpec.clearPassword()
            passwordChars.fill('\u0000')
        }
    }

    fun matches(password: String, salt: ByteArray, storedHash: ByteArray): Boolean =
        MessageDigest.isEqual(hash(password, salt), storedHash)
}
