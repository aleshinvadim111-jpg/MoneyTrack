package com.example.moneytrack.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

/** Даты подписок: сдвиг на следующий период и подсчёт дней до списания */
class DatesTest {

    private fun date(year: Int, month: Int, day: Int, hour: Int = 12): Long =
        Calendar.getInstance().apply {
            clear()
            set(year, month, day, hour, 0, 0)
        }.timeInMillis

    @Test
    fun addPeriod_monthly_movesToSameDayNextMonth() {
        assertEquals(date(2026, Calendar.NOVEMBER, 15), Dates.addPeriod(date(2026, Calendar.OCTOBER, 15), yearly = false))
        // декабрь → январь следующего года
        assertEquals(date(2027, Calendar.JANUARY, 5), Dates.addPeriod(date(2026, Calendar.DECEMBER, 5), yearly = false))
    }

    @Test
    fun addPeriod_monthly_from31st_movesToLastDayOfShortMonth() {
        // 31 января + месяц = 28 февраля (а не 3 марта)
        assertEquals(date(2027, Calendar.FEBRUARY, 28), Dates.addPeriod(date(2027, Calendar.JANUARY, 31), yearly = false))
    }

    @Test
    fun addPeriod_yearly_movesOneYear() {
        assertEquals(date(2027, Calendar.OCTOBER, 8), Dates.addPeriod(date(2026, Calendar.OCTOBER, 8), yearly = true))
    }

    @Test
    fun daysBetween_countsCalendarDays() {
        assertEquals(0, Dates.daysBetween(date(2026, Calendar.OCTOBER, 8, hour = 1), date(2026, Calendar.OCTOBER, 8, hour = 23)))
        // 23:00 → 01:00 следующего дня — это уже «завтра», хотя прошло 2 часа
        assertEquals(1, Dates.daysBetween(date(2026, Calendar.OCTOBER, 8, hour = 23), date(2026, Calendar.OCTOBER, 9, hour = 1)))
        assertEquals(31, Dates.daysBetween(date(2026, Calendar.OCTOBER, 1), date(2026, Calendar.NOVEMBER, 1)))
        assertEquals(-1, Dates.daysBetween(date(2026, Calendar.OCTOBER, 9), date(2026, Calendar.OCTOBER, 8)))
    }
}
