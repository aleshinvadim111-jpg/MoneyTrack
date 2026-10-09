package com.example.moneytrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.moneytrack.data.MoneyRepository
import com.example.moneytrack.model.Expense
import kotlinx.coroutines.flow.StateFlow

/** Статистика: отдаёт экрану список трат, месяц и суммы экран считает сам */
class StatsViewModel(repo: MoneyRepository) : ViewModel() {
    val expenses: StateFlow<List<Expense>> = repo.expenses
}
