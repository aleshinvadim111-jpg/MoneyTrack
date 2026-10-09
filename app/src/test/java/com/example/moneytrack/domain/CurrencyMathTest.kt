package com.example.moneytrack.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Пересчёт рублей в валюту и форматирование курсов ЦБ */
class CurrencyMathTest {

    @Test
    fun rubToForeign_roundsToWholeUnits() {
        assertEquals(134L, CurrencyMath.rubToForeign(12_400, 92.5)) // 134,05 → 134
        assertEquals(100L, CurrencyMath.rubToForeign(9_250, 92.5))
    }

    @Test
    fun rubToForeign_invalidRate_returnsNull() {
        assertNull(CurrencyMath.rubToForeign(12_400, 0.0))
        assertNull(CurrencyMath.rubToForeign(12_400, -1.0))
    }

    @Test
    fun formatRate_usesRussianDecimalComma() {
        assertEquals("92,50", CurrencyMath.formatRate(92.5))
        assertEquals("12,35", CurrencyMath.formatRate(12.3456))
    }

    @Test
    fun formatChange_showsDirectionArrow() {
        assertEquals("▲ 0,35", CurrencyMath.formatChange(0.35))
        assertEquals("▼ 0,12", CurrencyMath.formatChange(-0.12))
        assertEquals("0,00", CurrencyMath.formatChange(0.0))
        assertEquals("0,00", CurrencyMath.formatChange(0.003)) // меньше копейки — без стрелки
    }

    @Test
    fun formatCbrDate_convertsIsoToRussianFormat() {
        assertEquals("08.10.2026", CurrencyMath.formatCbrDate("2026-10-08T11:30:00+03:00"))
        assertEquals("кривая дата", CurrencyMath.formatCbrDate("кривая дата"))
    }

    @Test
    fun isStale_cacheLivesOneHour() {
        val now = 10_000_000L
        assertTrue(CurrencyMath.isStale(updatedAt = null, now = now))                      // курсов ещё нет
        assertFalse(CurrencyMath.isStale(updatedAt = now - 30 * 60 * 1000L, now = now))    // 30 минут назад
        assertTrue(CurrencyMath.isStale(updatedAt = now - 61 * 60 * 1000L, now = now))     // больше часа назад
    }
}
