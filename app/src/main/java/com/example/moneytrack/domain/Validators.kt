package com.example.moneytrack.domain

/**
 * Правила проверки введённых данных.
 * Чистый Kotlin без Android-классов — поэтому их легко проверить юнит-тестами.
 */
object Validators {
    const val PASSWORD_MIN_LENGTH = 8
    const val COMMENT_MAX_LENGTH = 200

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isNameValid(name: String): Boolean = name.isNotBlank()

    fun isEmailValid(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    /** Минимум 8 символов, хотя бы одна буква и одна цифра */
    fun isPasswordValid(password: String): Boolean =
        password.length >= PASSWORD_MIN_LENGTH &&
            password.any { it.isLetter() } &&
            password.any { it.isDigit() }

    fun passwordsMatch(password: String, repeat: String): Boolean = password == repeat

    /** Сумма из поля ввода: только положительное целое число, иначе null */
    fun parseAmount(input: String): Long? = input.trim().toLongOrNull()?.takeIf { it > 0 }
}
