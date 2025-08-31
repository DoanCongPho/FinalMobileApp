// com.example.finalproject.auth.register.viewmodel/RegisterViewModel.kt
package com.example.finalproject.auth.register.viewmodel

import androidx.lifecycle.ViewModel
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
    val totalSteps: Int = 6,
    val isLoading: Boolean = false,
    val error: String? = null
)

class RegisterViewModel(private val repo: RegisterRepository) : ViewModel() {
    private val _ui = MutableStateFlow(RegisterUiState())
    val ui: StateFlow<RegisterUiState> = _ui

    // updates
    fun updateStudentId(v: String) = _ui.update { it.copy(data = it.data.copy(studentId = v)) }
    fun updateFullName(v: String) = _ui.update { it.copy(data = it.data.copy(fullName = v)) }
    fun updateStudyField(v: String) = _ui.update { it.copy(data = it.data.copy(studyField = v)) }
    fun updateGender(g: com.example.finalproject.auth.register.model.Gender) =
        _ui.update { it.copy(data = it.data.copy(gender = g)) }
    fun updateMode(m: com.example.finalproject.auth.register.model.Mode) =
        _ui.update { it.copy(data = it.data.copy(mode = m)) }
    fun setAcceptedPrivacy(v: Boolean) = _ui.update { it.copy(data = it.data.copy(acceptedPrivacy = v)) }

    // navigation
    fun canGoNext(): Boolean {
        val s = _ui.value
        return when (s.currentStep) {
            0 -> s.data.studentId.isNotBlank()
            1 -> s.data.fullName.trim().length >= 2
            2 -> s.data.studyField.isNotBlank()
            3 -> s.data.gender != null
            4 -> s.data.mode != null
            5 -> s.data.acceptedPrivacy
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

    // submit to backend
    fun submit(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val s = _ui.value
        if (s.currentStep != s.totalSteps - 1 || !s.data.acceptedPrivacy) return
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, error = null) }
            val res = repo.register(s.data) // suspend call
            _ui.update { it.copy(isLoading = false) }
            if (res.isSuccess) {
                onSuccess()
            } else {
                val msg = res.exceptionOrNull()?.message ?: "Đăng ký thất bại"
                _ui.update { it.copy(error = msg) }
                onError(msg)
            }
        }
    }
}



