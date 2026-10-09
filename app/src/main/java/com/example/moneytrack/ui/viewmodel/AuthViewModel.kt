package com.example.moneytrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrack.data.AuthResult
import com.example.moneytrack.data.MoneyRepository
import com.example.moneytrack.domain.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Вход и регистрация: вызывает репозиторий и хранит текст ошибки для экрана */
class AuthViewModel(private val repo: MoneyRepository) : ViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (_loading.value) return // защита от двойного нажатия
        viewModelScope.launch {
            _loading.value = true
            val result = repo.login(email, password)
            _loading.value = false
            handle(result, onSuccess)
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        if (_loading.value) return
        // Повторная проверка: экран не пропустит неверные данные, но ViewModel не должна на это полагаться
        if (!Validators.isNameValid(name) || !Validators.isEmailValid(email) || !Validators.isPasswordValid(password)) {
            _error.value = "Проверьте введённые данные"
            return
        }
        viewModelScope.launch {
            _loading.value = true
            val result = repo.register(name, email, password)
            _loading.value = false
            handle(result, onSuccess)
        }
    }

    fun clearError() {
        _error.value = null
    }

    private fun handle(result: AuthResult, onSuccess: () -> Unit) {
        when (result) {
            AuthResult.Success -> {
                _error.value = null
                onSuccess()
            }
            is AuthResult.Error -> _error.value = result.message
        }
    }
}
