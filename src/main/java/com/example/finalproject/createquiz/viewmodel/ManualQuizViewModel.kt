package com.example.finalproject.createquiz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.example.finalproject.createquiz.model.ManualQuestion
import com.example.finalproject.core.network.api.ApiClient
import com.example.finalproject.core.DataStore.TokenManager
import com.example.finalproject.createquiz.model.QuizCreateRequest
import com.example.finalproject.createquiz.model.QuizQuestionCreateRequest
import com.example.finalproject.journey.model.Quiz

data class ManualUiState(
    val items: List<ManualQuestion> = emptyList(),
    val title: String = "",
    val draftQuestion: String = "",
    val draftAnswer: String = "",
    val canSubmit: Boolean = false,
    val showEmptyError: Boolean = false,
    val showTitleError: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val createdQuizId: Int? = null
)

class ManualQuizViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val tokenManager = TokenManager.getInstance(application.applicationContext)
    private val apiClient = ApiClient.create(tokenManager)
    private val quizApi = apiClient.quizApi

    private val _state = MutableStateFlow(ManualUiState())
    val state: StateFlow<ManualUiState> = _state

    fun onTitleChange(text: String) {
        _state.update { 
            it.copy(
                title = text, 
                showTitleError = false, 
                canSubmit = canSubmitInternal(it.copy(title = text))
            ) 
        }
    }

    fun onQuestionChange(text: String) {
        _state.update { it.copy(draftQuestion = text, showEmptyError = false, canSubmit = canSubmitInternal(it.copy(draftQuestion = text))) }
    }

    fun onAnswerChange(text: String) {
        _state.update { it.copy(draftAnswer = text, showEmptyError = false, canSubmit = canSubmitInternal(it.copy(draftAnswer = text))) }
    }

    fun addFlashcard() {
        val s = _state.value
        if (s.draftQuestion.isBlank() || s.draftAnswer.isBlank()) {
            _state.update { it.copy(showEmptyError = true) }
            return
        }
        val newItem = ManualQuestion(question = s.draftQuestion.trim(), answer = s.draftAnswer.trim())
        _state.update {
            it.copy(
                items = it.items + newItem,
                draftQuestion = "",
                draftAnswer = "",
                canSubmit = canSubmitInternal(it.copy(items = it.items + newItem, draftQuestion = "", draftAnswer = ""))
            )
        }
    }

    fun removeFlashcard(id: String) {
        _state.update { old ->
            val next = old.items.filterNot { it.id == id }
            old.copy(items = next, canSubmit = canSubmitInternal(old.copy(items = next)))
        }
    }

    private fun canSubmitInternal(s: ManualUiState): Boolean {
        return s.items.isNotEmpty() && s.title.isNotBlank()
    }

    fun currentFlashcards(): List<ManualQuestion> = _state.value.items
    
    // Real API implementation
    fun submitQuiz(onSuccess: (Int) -> Unit) {
        val s = _state.value
        if (!s.canSubmit) return
        
        // Check if title is provided
        if (s.title.isBlank()) {
            _state.update { it.copy(showTitleError = true) }
            return
        }
        
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            
            try {
                // Get access token
                val token = tokenManager.accessToken.first()
                
                if (token.isNullOrBlank()) {
                    _state.update { 
                        it.copy(
                            isSubmitting = false, 
                            error = "Authentication required. Please log in again."
                        ) 
                    }
                    return@launch
                }
                
                // Convert ManualQuestion to QuizQuestionCreateRequest
                val questions = s.items.map { question ->
                    QuizQuestionCreateRequest(
                        question = question.question,
                        answer = question.answer,
                        explanation = null
                    )
                }
                
                // Create the quiz request
                val request = QuizCreateRequest(
                    title = s.title.trim(),
                    questions = questions
                )
                
                // Make the API call
                val response = quizApi.createQuiz(request)
                
                if (response.isSuccessful) {
                    val quiz = response.body()
                    val quizId = quiz?.id ?: 0
                    
                    _state.update { 
                        it.copy(
                            isSubmitting = false, 
                            createdQuizId = quizId,
                            items = listOf(), // Clear form
                            draftQuestion = "",
                            draftAnswer = "",
                            canSubmit = false
                        ) 
                    }
                    onSuccess(quizId)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to create quiz"
                    
                    _state.update { 
                        it.copy(
                            isSubmitting = false, 
                            error = "Server Error (${response.code()}): $errorMsg"
                        ) 
                    }
                }
                
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isSubmitting = false, 
                        error = "Network error: ${e.message ?: "Unknown error occurred"}"
                    ) 
                }
            }
        }
    }
}
