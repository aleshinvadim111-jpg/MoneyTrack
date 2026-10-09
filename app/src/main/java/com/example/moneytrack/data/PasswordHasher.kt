package com.example.moneytrack.data

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Пароль никогда не хранится открытым текстом.
 * В базу пишется только соль и хэш PBKDF2 (10 000 итераций).
 */
object PasswordHasher {
    private const val ITERATIONS = 10_000
    private const val KEY_LENGTH = 256

    fun newSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    fun hash(password: String, saltHex: String): String {
        val spec = PBEKeySpec(password.toCharArray(), saltHex.hexToBytes(), ITERATIONS, KEY_LENGTH)
        val bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded
        spec.clearPassword()
        return bytes.toHex()
    }

    fun verify(password: String, saltHex: String, expectedHash: String): Boolean =
        MessageDigest.isEqual(hash(password, saltHex).toByteArray(), expectedHash.toByteArray())

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun String.hexToBytes(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
