package com.example.moneytrack.ui.components

import kotlin.math.abs

private const val NBSP = '\u00A0' // неразрывный пробел: сумма не переносится на две строки

/** 12400 -> "12 400" */
fun groupDigits(value: Long): String {
    val grouped = abs(value).toString().reversed().chunked(3).joinToString(NBSP.toString()).reversed()
    return if (value < 0) "−$grouped" else grouped
}

/** 12400 -> "12 400 ₽" */
fun rub(value: Long): String = "${groupDigits(value)}$NBSP₽"

/** 850 -> "850,00" — формат строки в чеке */
fun receiptAmount(value: Long): String = "${groupDigits(value)},00"

/** 1 день, 2 дня, 5 дней */
fun pluralDays(n: Int): String = when {
    n % 10 == 1 && n % 100 != 11 -> "день"
    n % 10 in 2..4 && n % 100 !in 12..14 -> "дня"
    else -> "дней"
}
