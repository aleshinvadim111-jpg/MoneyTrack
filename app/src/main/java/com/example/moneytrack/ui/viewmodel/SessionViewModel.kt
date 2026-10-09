package com.example.moneytrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrack.data.MoneyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Запуск приложения: проверяет сохранённую сессию.
 * null — ещё проверяем, true — сразу на Главную, false — на экран входа.
 * Значение выставляется один раз и дальше не меняется (от него зависит стартовый экран).
 */
class SessionViewModel(private val repo: MoneyRepository) : ViewModel() {
    private val _startLoggedIn = MutableStateFlow<Boolean?>(null)
    val startLoggedIn: StateFlow<Boolean?> = _startLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            _startLoggedIn.value = repo.restoreSession()
        }
    }
}
