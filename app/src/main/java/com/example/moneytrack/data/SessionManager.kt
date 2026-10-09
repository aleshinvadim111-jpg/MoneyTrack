package com.example.moneytrack.data

import android.content.Context

/** Запоминает, кто вошёл, чтобы после перезапуска сразу открывалась Главная */
class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    var userId: Long?
        get() = prefs.getLong(KEY_USER_ID, -1L).takeIf { it > 0 }
        set(value) {
            val editor = prefs.edit()
            if (value == null) editor.remove(KEY_USER_ID) else editor.putLong(KEY_USER_ID, value)
            editor.apply()
        }

    /** Переключатель «Уведомления» в Профиле (по умолчанию включены) */
    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS, value).apply()

    fun clear() {
        userId = null
    }

    private companion object {
        const val KEY_USER_ID = "user_id"
        const val KEY_NOTIFICATIONS = "notifications_enabled"
    }
}
