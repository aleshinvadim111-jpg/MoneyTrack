package com.example.moneytrack

import android.content.Context
import com.example.moneytrack.data.AppDatabase
import com.example.moneytrack.data.CurrencyRepository
import com.example.moneytrack.data.MoneyRepository
import com.example.moneytrack.data.SessionManager
import com.example.moneytrack.data.network.NetworkModule
import com.example.moneytrack.notifications.Notifier
import com.example.moneytrack.notifications.ReminderScheduler

/**
 * Контейнер зависимостей (ручной DI): все долгоживущие объекты создаются здесь один раз
 * и дальше передаются во ViewModel через AppViewModelFactory.
 */
class AppContainer(context: Context) {
    private val database = AppDatabase.build(context)

    val moneyRepository = MoneyRepository(database, SessionManager(context))
    val currencyRepository = CurrencyRepository(database.currencyDao(), NetworkModule.cbrApi)
    val notifier = Notifier(context)
    val reminderScheduler = ReminderScheduler(context)
}
