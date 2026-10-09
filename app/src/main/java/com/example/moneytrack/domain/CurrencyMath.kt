package com.example.moneytrack.domain

import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

/** Пересчёт и форматирование курсов. Чистый Kotlin — покрыт юнит-тестами. */
object CurrencyMath {
    /** Валюты, которые показываем, в нужном порядке */
    val TRACKED = listOf("USD", "EUR", "CNY")

    /** Кэш считается свежим час, потом при открытии экрана курс перезапрашивается */
    const val CACHE_TTL_MS = 60 * 60 * 1000L

    private val RU = Locale.forLanguageTag("ru")

    /** 12 400 ₽ при курсе 92,5 → 134 $. null, если курс некорректный. */
    fun rubToForeign(rub: Long, rubPerUnit: Double): Long? =
        if (rubPerUnit > 0) (rub / rubPerUnit).roundToLong() else null

    /** 92.5 → "92,50" */
    fun formatRate(value: Double): String = String.format(RU, "%.2f", value)

    /** +0.35 → "▲ 0,35", −0.12 → "▼ 0,12", 0 → "0,00" */
    fun formatChange(change: Double): String {
        val text = formatRate(abs(change))
        return when {
            change > 0.004 -> "▲ $text"
            change < -0.004 -> "▼ $text"
            else -> text
        }
    }

    /** "2026-10-08T11:30:00+03:00" → "08.10.2026" */
    fun formatCbrDate(iso: String): String {
        val parts = iso.take(10).split("-")
        return if (parts.size == 3) "${parts[2]}.${parts[1]}.${parts[0]}" else iso
    }

    fun isStale(updatedAt: Long?, now: Long): Boolean = updatedAt == null || now - updatedAt > CACHE_TTL_MS
}
