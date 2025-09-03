package com.example.finalproject.auth.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.auth.login.data.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel(private val repo: LoginRepository) : ViewModel() {
    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    fun updateUsername(v: String) = _ui.update { it.copy(username = v) }
    fun updatePassword(v: String) = _ui.update { it.copy(password = v) }

    fun canLogin(): Boolean {
        val s = _ui.value
        return s.username.isNotBlank() && s.password.isNotBlank()
    }

    fun login(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val s = _ui.value
        if (!canLogin()) return
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            val res = repo.login(s.username, s.password)
            _ui.update { it.copy(isLoading = false) }
            if (res.isSuccess) {
                onSuccess()
            } else {
                val msg = res.exceptionOrNull()?.message ?: "Login failed"
                _ui.update { it.copy(error = msg) }
                onError(msg)
            }
        }
    }
}
