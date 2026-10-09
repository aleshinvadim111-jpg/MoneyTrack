package com.example.moneytrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrack.data.MoneyRepository
import com.example.moneytrack.model.Subscription
import kotlinx.coroutines.launch

/** Форма подписки: создание, редактирование и удаление */
class SubscriptionFormViewModel(private val repo: MoneyRepository) : ViewModel() {
    private var saving = false

    fun find(id: Long?): Subscription? = id?.let(repo::findSubscription)

    /** id == 0 — новая подписка, иначе обновляем существующую */
    fun save(subscription: Subscription, onDone: () -> Unit) {
        if (saving || subscription.title.isBlank() || subscription.amount <= 0) return
        saving = true
        viewModelScope.launch {
            if (subscription.id == 0L) repo.addSubscription(subscription) else repo.updateSubscription(subscription)
            saving = false
            onDone()
        }
    }

    fun delete(subscription: Subscription, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.deleteSubscription(subscription)
            onDone()
        }
    }
}
