package com.example.moneytrack

import android.app.Application

/** Класс приложения: создаёт контейнер зависимостей, каналы уведомлений и ежедневные напоминания */
class MoneyTrackApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.notifier.createChannels()
        container.reminderScheduler.scheduleDaily()
    }
}
