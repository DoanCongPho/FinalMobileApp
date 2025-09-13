package com.example.finalproject.createquiz.viewmodel

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.createquiz.data.CreateQuizRepository
import com.example.finalproject.createquiz.model.Difficulty
import com.example.finalproject.createquiz.model.QuizSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CreateQuizUiState(
    val sources: List<QuizSource> = emptyList(),
    val prompt: String = "",
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val numQuestions: Int = 10,
    val isSubmitting: Boolean = false,
    val createdQuizId: String? = null,
    val error: String? = null,
    val isCreatingFromFile: Boolean = false,
    val createdQuiz: com.example.finalproject.journey.model.Quiz? = null
)

class CreateQuizViewModel(
    private val repo: CreateQuizRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(CreateQuizUiState())
    val ui: StateFlow<CreateQuizUiState> = _ui

    fun addSources(list: List<QuizSource>) {
        _ui.value = _ui.value.copy(
            sources = (_ui.value.sources + list).distinctBy { it.uriString }
        )
    }
    fun removeSource(uriString: String) {
        _ui.value = _ui.value.copy(sources = _ui.value.sources.filterNot { it.uriString == uriString })
    }

    fun setPrompt(p: String) = setState { copy(prompt = p) }
    fun setDifficulty(d: Difficulty) = setState { copy(difficulty = d) }
    fun setNumQuestions(n: Int) = setState { copy(numQuestions = n) }

    // NEW: Create quiz from single file using new backend API
    fun createQuizFromFile(resolver: ContentResolver, onSuccess: (Int) -> Unit) {
        val s = _ui.value
        if (s.sources.isEmpty()) { setError("Please select a file."); return }
        if (s.numQuestions <= 0) { setError("Number of questions must be > 0."); return }

        setState { copy(isSubmitting = true, isCreatingFromFile = true, error = null) }
        viewModelScope.launch {
            runCatching {
                // Use the first source file for the new API
                val source = s.sources.first()
                repo.createQuizFromFile(
                    resolver = resolver,
                    source = source,
                    prompt = s.prompt.takeIf { it.isNotBlank() },
                    numQuestions = s.numQuestions
                )
            }.onSuccess { quiz ->
                setState { 
                    copy(
                        isSubmitting = false, 
                        isCreatingFromFile = false,
                        createdQuiz = quiz,
                        createdQuizId = quiz.id.toString()
                    ) 
                }
                onSuccess(quiz.id)
            }.onFailure {
                setState { 
                    copy(
                        isSubmitting = false, 
                        isCreatingFromFile = false,
                        error = it.message ?: "Failed to create quiz from file"
                    ) 
                }
            }
        }
    }

    // Legacy method for backward compatibility
    fun submit(resolver: ContentResolver, onSuccess: (String) -> Unit) {
        val s = _ui.value
        if (s.sources.isEmpty()) { setError("Please select at least one file."); return }
        if (s.numQuestions <= 0) { setError("Number of questions must be > 0."); return }

        setState { copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            runCatching {
                repo.generateQuiz(resolver, s.sources, s.prompt, s.difficulty, s.numQuestions)
            }.onSuccess {
                setState { copy(isSubmitting = false, createdQuizId = it.quizId) }
                onSuccess(it.quizId)
            }.onFailure {
                setState { copy(isSubmitting = false, error = it.message ?: "Unknown error") }
            }
        }
    }

    private inline fun setState(block: CreateQuizUiState.() -> CreateQuizUiState) {
        _ui.value = _ui.value.block()
    }
    private fun setError(msg: String) = setState { copy(error = msg) }
}