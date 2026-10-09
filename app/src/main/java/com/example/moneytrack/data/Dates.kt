package com.example.moneytrack.data

import java.util.Calendar
import java.util.TimeZone

const val DAY_MS = 86_400_000L

/** Работа с датами: всё хранится в миллисекундах (местное время) */
object Dates {
    fun startOfDay(millis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    /** Сколько календарных дней от from до to */
    fun daysBetween(from: Long, to: Long): Int =
        Math.round((startOfDay(to) - startOfDay(from)).toDouble() / DAY_MS).toInt()

    /** Момент «N дней назад в hh:mm» (отрицательное N — в будущем) */
    fun daysAgoAt(days: Int, hour: Int, minute: Int): Long = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -days)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    fun isSameMonth(millis: Long, year: Int, month: Int): Boolean {
        val c = Calendar.getInstance().apply { timeInMillis = millis }
        return c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH) == month
    }

    fun isThisMonth(millis: Long): Boolean {
        val now = Calendar.getInstance()
        return isSameMonth(millis, now.get(Calendar.YEAR), now.get(Calendar.MONTH))
    }

    /** Сдвиг даты подписки на один период */
    fun addPeriod(millis: Long, yearly: Boolean): Long = Calendar.getInstance().apply {
        timeInMillis = millis
        add(if (yearly) Calendar.YEAR else Calendar.MONTH, 1)
    }.timeInMillis

    /** Календарь Material отдаёт полночь по UTC → тот же день по местному времени, время суток — сейчас */
    fun pickerToLocal(utcMillis: Long): Long {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMillis }
        val now = Calendar.getInstance()
        return Calendar.getInstance().apply {
            set(
                utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH),
                now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), 0,
            )
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /** Обратно: местная дата → полночь UTC для календаря */
    fun localToPicker(localMillis: Long): Long {
        val local = Calendar.getInstance().apply { timeInMillis = localMillis }
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis
    }
}
