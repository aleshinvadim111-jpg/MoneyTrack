package com.example.moneytrack.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Проверка правил ввода на экранах регистрации, входа и новой траты */
class ValidatorsTest {

    @Test
    fun email_correctAddresses_areValid() {
        assertTrue(Validators.isEmailValid("aleshin@mail.ru"))
        assertTrue(Validators.isEmailValid("v.aleshin+test@voenmeh.ru"))
        // пробелы по краям обрезаются — частая ошибка при вводе с клавиатуры
        assertTrue(Validators.isEmailValid("  user@gmail.com  "))
    }

    @Test
    fun email_wrongAddresses_areInvalid() {
        assertFalse(Validators.isEmailValid(""))
        assertFalse(Validators.isEmailValid("aleshin"))
        assertFalse(Validators.isEmailValid("aleshin@"))
        assertFalse(Validators.isEmailValid("aleshin@mail"))
        assertFalse(Validators.isEmailValid("@mail.ru"))
        assertFalse(Validators.isEmailValid("ale shin@mail.ru"))
    }

    @Test
    fun password_lettersAndDigits8Chars_isValid() {
        assertTrue(Validators.isPasswordValid("qwerty12"))
        assertTrue(Validators.isPasswordValid("Пароль2026"))
    }

    @Test
    fun password_tooShortOrWithoutDigitsOrLetters_isInvalid() {
        assertFalse(Validators.isPasswordValid("qwer12"))      // 6 символов
        assertFalse(Validators.isPasswordValid("qwertyui"))    // нет цифр
        assertFalse(Validators.isPasswordValid("12345678"))    // нет букв
        assertFalse(Validators.isPasswordValid(""))
    }

    @Test
    fun passwordsMatch_comparesExactly() {
        assertTrue(Validators.passwordsMatch("qwerty12", "qwerty12"))
        assertFalse(Validators.passwordsMatch("qwerty12", "Qwerty12"))
        assertFalse(Validators.passwordsMatch("qwerty12", "qwerty12 "))
    }

    @Test
    fun name_blank_isInvalid() {
        assertTrue(Validators.isNameValid("Владимир"))
        assertFalse(Validators.isNameValid(""))
        assertFalse(Validators.isNameValid("   "))
    }

    @Test
    fun parseAmount_acceptsOnlyPositiveIntegers() {
        assertEquals(850L, Validators.parseAmount("850"))
        assertEquals(1200L, Validators.parseAmount(" 1200 "))
        assertNull(Validators.parseAmount("0"))
        assertNull(Validators.parseAmount("-50"))
        assertNull(Validators.parseAmount("12,5"))
        assertNull(Validators.parseAmount("abc"))
        assertNull(Validators.parseAmount(""))
    }
}
