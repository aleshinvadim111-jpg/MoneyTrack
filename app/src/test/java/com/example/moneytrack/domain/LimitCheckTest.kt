package com.example.moneytrack.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Проверка месячного лимита: цвет кольца, плашка на Главной и уведомления */
class LimitCheckTest {

    private val limit = 30_000L

    @Test
    fun level_belowWarningThreshold_isOk() {
        assertEquals(LimitLevel.OK, LimitCheck.level(spent = 0, limit = limit))
        assertEquals(LimitLevel.OK, LimitCheck.level(spent = 23_999, limit = limit))
    }

    @Test
    fun level_from80Percent_isWarning() {
        assertEquals(LimitLevel.WARNING, LimitCheck.level(spent = 24_000, limit = limit)) // ровно 80%
        assertEquals(LimitLevel.WARNING, LimitCheck.level(spent = 30_000, limit = limit)) // ровно 100% — ещё не превышен
    }

    @Test
    fun level_overLimit_isExceeded() {
        assertEquals(LimitLevel.EXCEEDED, LimitCheck.level(spent = 30_001, limit = limit))
    }

    @Test
    fun level_noLimitSet_isAlwaysOk() {
        assertEquals(LimitLevel.OK, LimitCheck.level(spent = 1_000_000, limit = 0))
    }

    @Test
    fun percent_isCalculatedCorrectly() {
        assertEquals(50, LimitCheck.percent(spent = 15_000, limit = limit))
        assertEquals(120, LimitCheck.percent(spent = 36_000, limit = limit))
        assertEquals(0, LimitCheck.percent(spent = 5_000, limit = 0))
    }

    @Test
    fun crossed_notifiesOnlyOnceWhenBorderIsCrossed() {
        // 70% → 85%: пересекли 80% — предупреждение
        assertEquals(LimitLevel.WARNING, LimitCheck.crossed(21_000, 25_500, limit))
        // 85% → 90%: уже были в WARNING — повторно не уведомляем
        assertNull(LimitCheck.crossed(25_500, 27_000, limit))
        // 90% → 110%: превысили лимит
        assertEquals(LimitLevel.EXCEEDED, LimitCheck.crossed(27_000, 33_000, limit))
        // 70% → 110% одной покупкой: сразу превышение
        assertEquals(LimitLevel.EXCEEDED, LimitCheck.crossed(21_000, 33_000, limit))
        // удаление траты (уровень понизился) — не уведомляем
        assertNull(LimitCheck.crossed(33_000, 20_000, limit))
    }
}
