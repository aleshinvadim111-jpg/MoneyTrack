package com.example.moneytrack.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.moneytrack.MainActivity
import com.example.moneytrack.R
import com.example.moneytrack.domain.LimitCheck
import com.example.moneytrack.domain.LimitLevel
import com.example.moneytrack.model.Subscription
import com.example.moneytrack.ui.components.rub

/** Все системные уведомления приложения */
class Notifier(private val context: Context) {

    /** Каналы создаются один раз при запуске (Android 8+). Пользователь может настроить их в системе. */
    fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_SUBSCRIPTIONS, "Подписки", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Напоминание за день до списания"
            }
        )
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_LIMIT, "Месячный лимит", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Когда траты подходят к лимиту или превышают его"
            }
        )
    }

    /** Есть ли разрешение показывать уведомления (на Android 13+ его выдаёт пользователь) */
    fun canNotify(): Boolean {
        val granted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        return granted && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun showSubscriptionReminder(sub: Subscription) {
        show(
            id = SUBSCRIPTION_ID_BASE + sub.id.toInt(),
            channel = CHANNEL_SUBSCRIPTIONS,
            title = "Завтра списание: ${sub.title}",
            text = "Спишется ${rub(sub.amount)}. Проверьте, хватает ли денег на карте.",
        )
    }

    fun showLimitWarning(level: LimitLevel, spent: Long, limit: Long) {
        val (title, text) = when (level) {
            LimitLevel.EXCEEDED -> "Лимит превышен" to
                "Потрачено ${rub(spent)} из ${rub(limit)} — перерасход ${rub(spent - limit)}."
            LimitLevel.WARNING -> "Потрачено ${LimitCheck.percent(spent, limit)}% лимита" to
                "Осталось ${rub(limit - spent)} до конца месяца."
            LimitLevel.OK -> return
        }
        show(LIMIT_ID, CHANNEL_LIMIT, title, text)
    }

    private fun show(id: Int, channel: String, title: String, text: String) {
        if (!canNotify()) return
        // Тап по уведомлению открывает приложение
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (e: SecurityException) {
            // разрешение отозвали между проверкой и показом — просто не показываем
        }
    }

    companion object {
        const val CHANNEL_SUBSCRIPTIONS = "subscriptions"
        const val CHANNEL_LIMIT = "limit"
        private const val LIMIT_ID = 1
        private const val SUBSCRIPTION_ID_BASE = 1000
    }
}
