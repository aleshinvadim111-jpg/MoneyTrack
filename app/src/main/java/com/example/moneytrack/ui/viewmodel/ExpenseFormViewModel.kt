package com.example.moneytrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrack.data.MoneyRepository
import com.example.moneytrack.domain.LimitCheck
import com.example.moneytrack.model.Category
import com.example.moneytrack.model.Expense
import com.example.moneytrack.notifications.Notifier
import kotlinx.coroutines.launch

/**
 * Форма расхода. Работает в двух режимах:
 * id == null — новый расход, id != null — редактирование существующего.
 */
class ExpenseFormViewModel(
    private val repo: MoneyRepository,
    private val notifier: Notifier,
) : ViewModel() {
    private var saving = false

    /** Трата для редактирования (null — форма создания) */
    fun find(id: Long?): Expense? = id?.let(repo::findExpense)

    fun save(
        editing: Expense?,
        category: Category,
        amount: Long,
        dateMillis: Long,
        note: String,
        onDone: () -> Unit,
    ) {
        if (saving || amount <= 0) return
        saving = true
        viewModelScope.launch {
            val spentBefore = repo.spentThisMonth()
            if (editing == null) {
                repo.addExpense(category, amount, dateMillis, note.trim())
            } else {
                repo.updateExpense(
                    editing.copy(category = category, amount = amount, dateMillis = dateMillis, note = note.trim())
                )
            }
            checkLimit(spentBefore)
            saving = false
            onDone()
        }
    }

    /** Если траты только что перешли 80% или 100% лимита — системное уведомление */
    private fun checkLimit(spentBefore: Long) {
        val limit = repo.user.value?.monthlyLimit ?: return
        val spentAfter = repo.spentThisMonth()
        val level = LimitCheck.crossed(spentBefore, spentAfter, limit) ?: return
        if (repo.notificationsEnabled.value) notifier.showLimitWarning(level, spentAfter, limit)
    }

    fun delete(expense: Expense, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.deleteExpense(expense)
            onDone()
        }
    }
}
