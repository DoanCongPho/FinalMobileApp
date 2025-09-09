// com.example.finalproject.auth.register.viewmodel/RegisterViewModel.kt
package com.example.finalproject.auth.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finalproject.auth.register.data.RegisterRepository
import com.example.finalproject.auth.register.model.RegistrationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val data: RegistrationData = RegistrationData(),
    val currentStep: Int = 0,
    val totalSteps: Int = 8,
    val isLoading: Boolean = false,
    val error: String? = null
)

class RegisterViewModel(private val repo: RegisterRepository) : ViewModel() {
    private val _ui = MutableStateFlow(RegisterUiState())
    val ui: StateFlow<RegisterUiState> = _ui
    fun updatePhone(v: String) = _ui.update { it.copy(data = it.data.copy(phoneNumber = v)) }
    fun updateGmail(v: String) = _ui.update { it.copy(data = it.data.copy(gmail = v)) }
    fun updateFullName(v: String) = _ui.update { it.copy(data = it.data.copy(fullName = v)) }
    fun updateStudyField(v: String) = _ui.update { it.copy(data = it.data.copy(studyField = v)) }
    fun updateGender(g: com.example.finalproject.auth.register.model.Gender) =
        _ui.update { it.copy(data = it.data.copy(gender = g)) }
    fun updateMode(m: com.example.finalproject.auth.register.model.Mode) =
        _ui.update { it.copy(data = it.data.copy(mode = m)) }
    fun setAcceptedPrivacy(v: Boolean) = _ui.update { it.copy(data = it.data.copy(acceptedPrivacy = v)) }
    fun updatePassword(v: String) = _ui.update {  it.copy(data = it.data.copy(password = v))}
    // navigation
    fun canGoNext(): Boolean {
        val s = _ui.value
        return when (s.currentStep) {
            0 -> s.data.fullName.trim().length >= 2
            1 -> s.data.studyField.isNotBlank()
            2 -> s.data.gender != null
            3 -> s.data.mode != null
            4 -> s.data.acceptedPrivacy
            5 -> s.data.phoneNumber.isNotBlank()
            6 -> s.data.gmail.isNotBlank()
            7 -> s.data.password.isNotBlank()
            else -> false
        }
    }

    fun next() {
        if (!canGoNext()) return
        _ui.update { it.copy(currentStep = (it.currentStep + 1).coerceAtMost(it.totalSteps - 1)) }
    }

    fun prev() {
        _ui.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(0)) }
    }

    fun submit(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val s = _ui.value
        if (s.currentStep != s.totalSteps - 1 || !s.data.acceptedPrivacy) return
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            val res = repo.register(s.data)
            _ui.update { it.copy(isLoading = false) }
            res.fold(
                onSuccess = {
                    onSuccess()
                },
                onFailure = { e ->
                    val msg = e.message ?: "Đăng ký thất bại"
                    _ui.update { it.copy(error = msg) }
                    onError(msg)
                }
            )
        }
    }
}


class RegisterViewModelFactory(
    private val repo: RegisterRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
