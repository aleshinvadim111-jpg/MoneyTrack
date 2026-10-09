package com.example.moneytrack.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Хэширование паролей: пароль не хранится в базе открытым текстом */
class PasswordHasherTest {

    @Test
    fun verify_correctPassword_returnsTrue() {
        val salt = PasswordHasher.newSalt()
        val hash = PasswordHasher.hash("qwerty12", salt)
        assertTrue(PasswordHasher.verify("qwerty12", salt, hash))
    }

    @Test
    fun verify_wrongPassword_returnsFalse() {
        val salt = PasswordHasher.newSalt()
        val hash = PasswordHasher.hash("qwerty12", salt)
        assertFalse(PasswordHasher.verify("qwerty13", salt, hash))
        assertFalse(PasswordHasher.verify("Qwerty12", salt, hash))
        assertFalse(PasswordHasher.verify("", salt, hash))
    }

    @Test
    fun hash_doesNotContainPasswordAndHasFixedLength() {
        val hash = PasswordHasher.hash("qwerty12", PasswordHasher.newSalt())
        assertFalse(hash.contains("qwerty12"))
        assertEquals(64, hash.length) // 256 бит = 32 байта = 64 hex-символа
    }

    @Test
    fun samePassword_differentSalt_givesDifferentHash() {
        // соль защищает от поиска одинаковых паролей у разных пользователей
        val hash1 = PasswordHasher.hash("qwerty12", PasswordHasher.newSalt())
        val hash2 = PasswordHasher.hash("qwerty12", PasswordHasher.newSalt())
        assertNotEquals(hash1, hash2)
    }
}
