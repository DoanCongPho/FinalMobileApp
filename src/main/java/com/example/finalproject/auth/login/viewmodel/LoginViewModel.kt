package com.example.finalproject.auth.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finalproject.auth.login.data.LoginRepository
import com.example.finalproject.core.DataStore.TokenManager
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
class LoginViewModel(
    private val repo: LoginRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
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
                val token = res.getOrNull()?.access_token ?: ""
                tokenManager.clear()
                tokenManager.saveAccessToken(token)
                val userRes = repo.fetchUserProfile()
                if (userRes.isSuccess) {
                    val user = userRes.getOrNull()
                    tokenManager.saveUserId(user?.id ?: 0)
                    tokenManager.saveUserProfile(
                        name = user?.name,
                        email = user?.email,
                        phone = user?.phoneNumber
                    )
                }
                onSuccess()
            }
            else {
                val e = res.exceptionOrNull()
                val msg = when (e) {
                    is retrofit2.HttpException -> {
                        when (e.code()) {
                            401 -> "Invalid username or password"
                            500 -> "Server error, please try again later"
                            else -> "Unexpected error (${e.code()})"
                        }
                    }
                    else -> "Login failed"
                }
                _ui.update { it.copy(error = msg) }
                onError(msg)
            }
        }
    }



}

class LoginViewModelFactory(
    private val repo: LoginRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repo, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}