package com.example.moneytrack.domain

import com.example.moneytrack.data.Dates
import com.example.moneytrack.model.Subscription

/** Какие подписки нужно напомнить сегодня */
object ReminderRules {
    /** За сколько дней до списания напоминаем («Напомнить за день») */
    const val DAYS_BEFORE = 1

    fun dueForReminder(subscriptions: List<Subscription>, now: Long): List<Subscription> =
        subscriptions.filter { it.remind && Dates.daysBetween(now, it.nextChargeMillis) == DAYS_BEFORE }
}
