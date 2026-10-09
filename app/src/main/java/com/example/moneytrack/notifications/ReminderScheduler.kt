package com.example.moneytrack.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/** Планирование напоминаний через WorkManager */
class ReminderScheduler(context: Context) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Ежедневная проверка, первый запуск — в ближайшие 10:00.
     * KEEP: если задача уже запланирована, повторный вызов при старте приложения её не сбрасывает.
     */
    fun scheduleDaily() {
        val request = PeriodicWorkRequestBuilder<SubscriptionReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(millisUntilNext(REMIND_HOUR), TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    /** Разовая проверка прямо сейчас — кнопка в Профиле, удобно показать на защите */
    fun runNow() {
        workManager.enqueue(OneTimeWorkRequestBuilder<SubscriptionReminderWorker>().build())
    }

    private fun millisUntilNext(hour: Int): Long {
        val now = Calendar.getInstance()
        val next = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (!after(now)) add(Calendar.DAY_OF_YEAR, 1)
        }
        return next.timeInMillis - now.timeInMillis
    }

    private companion object {
        const val WORK_NAME = "subscription_reminders"
        const val REMIND_HOUR = 10
    }
}
