package com.example.moneytrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrack.data.MoneyRepository
import com.example.moneytrack.model.Expense
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** История трат: список, удаление свайпом и «Отменить» */
class HistoryViewModel(private val repo: MoneyRepository) : ViewModel() {
    val expenses: StateFlow<List<Expense>> = repo.expenses

    fun delete(expense: Expense) {
        viewModelScope.launch { repo.deleteExpense(expense) }
    }

    fun restore(expense: Expense) {
        viewModelScope.launch { repo.restoreExpense(expense) }
    }
}
