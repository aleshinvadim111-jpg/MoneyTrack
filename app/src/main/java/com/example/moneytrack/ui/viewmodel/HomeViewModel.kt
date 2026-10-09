package com.example.moneytrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrack.data.CurrencyRepository
import com.example.moneytrack.data.Dates
import com.example.moneytrack.data.MoneyRepository
import com.example.moneytrack.model.CurrencyRate
import com.example.moneytrack.model.Expense
import com.example.moneytrack.model.Subscription
import com.example.moneytrack.model.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Всё, что показывает Главная, одним объектом */
data class HomeUiState(
    val userName: String = "",
    val initials: String = "",
    val spent: Long = 0,
    val limit: Long = 0,
    val todayExpenses: List<Expense> = emptyList(),
    val nearestSubscription: Subscription? = null,
    val usdRate: Double? = null, // null — курс ещё ни разу не загружался
)

/** Собирает состояние Главной из пользователя, трат, подписок и курсов */
fun buildHomeState(
    user: User?,
    expenses: List<Expense>,
    subscriptions: List<Subscription>,
    rates: List<CurrencyRate>,
) = HomeUiState(
    userName = user?.firstName ?: "",
    initials = user?.initials ?: "",
    spent = expenses.filter { Dates.isThisMonth(it.dateMillis) }.sumOf { it.amount },
    limit = user?.monthlyLimit ?: 0,
    todayExpenses = expenses.filter { it.daysAgo == 0 },
    nearestSubscription = subscriptions.minByOrNull { it.daysUntil },
    usdRate = rates.firstOrNull { it.code == "USD" }?.perUnit,
)

class HomeViewModel(repo: MoneyRepository, private val currency: CurrencyRepository) : ViewModel() {
    val uiState: StateFlow<HomeUiState> =
        combine(repo.user, repo.expenses, repo.subscriptions, currency.rates) { user, expenses, subs, rates ->
            buildHomeState(user, expenses, subs, rates)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = buildHomeState(repo.user.value, repo.expenses.value, repo.subscriptions.value, emptyList()),
        )

    init {
        // Обновляем курс при открытии Главной (если кэш старше часа)
        viewModelScope.launch { currency.refresh() }
    }
}
