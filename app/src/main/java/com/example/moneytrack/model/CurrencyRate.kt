package com.example.moneytrack.model

/** Курс валюты для экранов */
data class CurrencyRate(
    val code: String,      // USD
    val name: String,      // Доллар США
    val nominal: Int,      // за сколько единиц указан курс
    val value: Double,     // рублей за nominal единиц
    val previous: Double,  // курс на прошлый день
    val cbrDate: String,   // на какую дату курс установлен ЦБ
    val updatedAt: Long,   // когда мы его скачали
) {
    /** Рублей за 1 единицу валюты */
    val perUnit: Double get() = value / nominal

    /** Изменение за день (за 1 единицу) */
    val change: Double get() = (value - previous) / nominal
}
