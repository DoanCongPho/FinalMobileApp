package com.example.finalproject.journey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.finalproject.journey.data.QuizRepository
import com.example.finalproject.journey.model.Quiz
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {
    
    private val _quizzes = mutableStateOf<List<Quiz>>(emptyList())
    val quizzes: State<List<Quiz>> = _quizzes

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun loadQuizzes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val response = repository.getUserQuizzes()
                if (response.isSuccessful) {
                    val quizzes = response.body() ?: emptyList()
                    _quizzes.value = quizzes
                } else {
                    _errorMessage.value = "Failed to load quizzes: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading quizzes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getTotalQuestions(): Int {
        return _quizzes.value.sumOf { it.questions.size }
    }

    fun getQuizById(quizId: Int): Quiz? {
        return _quizzes.value.find { it.id == quizId }
    }

    fun deleteQuiz(quizId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val response = repository.deleteQuiz(quizId)
                if (response.isSuccessful) {
                    // Remove quiz from local list
                    _quizzes.value = _quizzes.value.filter { it.id != quizId }
                } else {
                    _errorMessage.value = "Failed to delete quiz: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting quiz: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class QuizViewModelFactory(private val repository: QuizRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}