package com.example.moneytrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrack.data.CurrencyRepository
import com.example.moneytrack.data.MoneyRepository
import com.example.moneytrack.data.RatesStatus
import com.example.moneytrack.model.CurrencyRate
import com.example.moneytrack.model.User
import com.example.moneytrack.notifications.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Блок «Курсы валют» в Профиле */
data class RatesUiState(
    val rates: List<CurrencyRate> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
) {
    val updatedAt: Long? get() = rates.maxOfOrNull { it.updatedAt }
    val cbrDate: String? get() = rates.firstOrNull()?.cbrDate?.takeIf { it.isNotBlank() }
}

/** Профиль: лимит, курсы валют, примерные данные, выход */
class ProfileViewModel(
    private val repo: MoneyRepository,
    private val currency: CurrencyRepository,
    private val reminders: ReminderScheduler,
) : ViewModel() {
    val user: StateFlow<User?> = repo.user
    val notificationsEnabled: StateFlow<Boolean> = repo.notificationsEnabled

    val rates: StateFlow<RatesUiState> =
        combine(currency.rates, currency.status) { list, status ->
            RatesUiState(
                rates = list,
                loading = status is RatesStatus.Loading,
                error = (status as? RatesStatus.Error)?.message,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RatesUiState(loading = true))

    init {
        viewModelScope.launch { currency.refresh() }
    }

    /** Кнопка «Обновить» — качаем курс, даже если кэш свежий */
    fun refreshRates() {
        viewModelScope.launch { currency.refresh(force = true) }
    }

    fun setNotificationsEnabled(enabled: Boolean) = repo.setNotificationsEnabled(enabled)

    /** Проверить напоминания прямо сейчас, не дожидаясь 10:00 */
    fun checkRemindersNow() = reminders.runNow()

    fun setLimit(limit: Long) {
        if (limit <= 0) return
        viewModelScope.launch { repo.setLimit(limit) }
    }

    fun fillDemoData() {
        viewModelScope.launch { repo.fillDemoData() }
    }

    fun logout() {
        repo.logout()
    }
}
