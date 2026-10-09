package com.example.moneytrack.domain

import androidx.compose.ui.graphics.Color
import com.example.moneytrack.model.SubPeriod
import com.example.moneytrack.model.Subscription
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

/** Какие подписки попадут в ежедневное напоминание WorkManager */
class ReminderRulesTest {

    private val now: Long = Calendar.getInstance().apply {
        clear()
        set(2026, Calendar.OCTOBER, 8, 10, 0, 0) // 08.10.2026, 10:00 — время запуска воркера
    }.timeInMillis

    private fun inDays(days: Int): Long = Calendar.getInstance().apply {
        timeInMillis = now
        add(Calendar.DAY_OF_YEAR, days)
    }.timeInMillis

    private fun sub(id: Long, title: String, days: Int, remind: Boolean = true) = Subscription(
        id = id,
        title = title,
        amount = 299,
        nextChargeMillis = inDays(days),
        period = SubPeriod.MONTH,
        color = Color(0xFF000000),
        remind = remind,
    )

    @Test
    fun dueForReminder_returnsOnlyTomorrowWithRemindOn() {
        val subs = listOf(
            sub(1, "Яндекс Плюс", days = 1),                 // завтра — напомнить
            sub(2, "Кинопоиск", days = 1, remind = false),   // завтра, но напоминание выключено
            sub(3, "VK Музыка", days = 0),                   // сегодня — поздно
            sub(4, "iCloud", days = 5),                      // рано
        )
        val due = ReminderRules.dueForReminder(subs, now)
        assertEquals(listOf("Яндекс Плюс"), due.map { it.title })
    }

    @Test
    fun dueForReminder_emptyList_returnsEmpty() {
        assertEquals(emptyList<Subscription>(), ReminderRules.dueForReminder(emptyList(), now))
    }
}
