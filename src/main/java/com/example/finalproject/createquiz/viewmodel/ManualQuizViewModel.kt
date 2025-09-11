package com.example.finalproject.createquiz.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.finalproject.createquiz.model.ManualQuestion

data class ManualUiState(
    val items: List<ManualQuestion> = emptyList(),
    val draftQuestion: String = "",
    val draftAnswer: String = "",
    val canSubmit: Boolean = false,
    val showEmptyError: Boolean = false
)

class ManualQuizViewModel : ViewModel() {

    private val _state = MutableStateFlow(ManualUiState())
    val state: StateFlow<ManualUiState> = _state

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
        return s.items.isNotEmpty()
    }

    fun currentFlashcards(): List<ManualQuestion> = _state.value.items
}
