package com.example.moneytrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.moneytrack.data.MoneyRepository
import com.example.moneytrack.model.Subscription
import kotlinx.coroutines.flow.StateFlow

/** Список подписок */
class SubscriptionsViewModel(repo: MoneyRepository) : ViewModel() {
    val subscriptions: StateFlow<List<Subscription>> = repo.subscriptions
}
