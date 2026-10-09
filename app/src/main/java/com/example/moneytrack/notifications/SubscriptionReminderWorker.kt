package com.example.moneytrack.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moneytrack.MoneyTrackApplication
import com.example.moneytrack.domain.ReminderRules

/**
 * Фоновая задача WorkManager: раз в день проверяет подписки
 * и присылает уведомление о тех, что спишутся завтра.
 * Работает, даже если приложение закрыто.
 */
class SubscriptionReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val container = (applicationContext as MoneyTrackApplication).container
        val repo = container.moneyRepository
        if (!repo.notificationsEnabled.value) return Result.success()

        val due = ReminderRules.dueForReminder(repo.subscriptionsForReminders(), System.currentTimeMillis())
        due.forEach { container.notifier.showSubscriptionReminder(it) }
        return Result.success()
    }
}
