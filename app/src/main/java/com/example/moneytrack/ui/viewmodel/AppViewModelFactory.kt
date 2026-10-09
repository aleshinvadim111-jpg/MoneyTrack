package com.example.moneytrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moneytrack.AppContainer

/**
 * Фабрика ViewModel: берёт зависимости из AppContainer и передаёт их в конструкторы.
 * Это ручное внедрение зависимостей — все объекты создаются в одном месте.
 */
class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = container.moneyRepository
        val currency = container.currencyRepository
        return with(modelClass) {
            when {
                isAssignableFrom(SessionViewModel::class.java) -> SessionViewModel(repo)
                isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repo)
                isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repo, currency)
                isAssignableFrom(StatsViewModel::class.java) -> StatsViewModel(repo)
                isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(repo)
                isAssignableFrom(SubscriptionsViewModel::class.java) -> SubscriptionsViewModel(repo)
                isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(repo, currency, container.reminderScheduler)
                isAssignableFrom(ExpenseFormViewModel::class.java) -> ExpenseFormViewModel(repo, container.notifier)
                isAssignableFrom(SubscriptionFormViewModel::class.java) -> SubscriptionFormViewModel(repo)
                else -> throw IllegalArgumentException("Неизвестная ViewModel: ${modelClass.name}")
            }
        } as T
    }
}
