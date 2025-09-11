package com.example.finalproject.createquiz.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import com.example.finalproject.createquiz.model.ManualQuestion

data class ManualQuizUiState(
    val items: List<ManualQuestion> = listOf(ManualQuestion(UUID.randomUUID().toString()))
)

class ManualQuizViewModel : ViewModel() {
    private val _ui = MutableStateFlow(ManualQuizUiState())
    val ui: StateFlow<ManualQuizUiState> = _ui

    fun addQuestion() {
        _ui.value = _ui.value.copy(
            items = _ui.value.items + ManualQuestion(UUID.randomUUID().toString())
        )
    }

    fun removeQuestion(id: String) {
        _ui.value = _ui.value.copy(items = _ui.value.items.filterNot { it.id == id })
        if (_ui.value.items.isEmpty()) addQuestion()
    }

    fun setQuestionText(id: String, text: String) {
        _ui.value = _ui.value.copy(
            items = _ui.value.items.map { if (it.id == id) it.copy(question = text) else it }
        )
    }

    fun setAnswerText(id: String, idx: Int, text: String) {
        val updated = _ui.value.items.map { q ->
            if (q.id == id) {
                val newAns = q.answers.toMutableList()
                while (idx >= newAns.size) newAns.add("")
                newAns[idx] = text
                q.copy(answers = newAns)
            } else q
        }
        _ui.value = _ui.value.copy(items = updated)
    }

    fun addAnswer(id: String) {
        _ui.value = _ui.value.copy(
            items = _ui.value.items.map { q ->
                if (q.id == id) q.copy(answers = (q.answers + "" ).toMutableList()) else q
            }
        )
    }

    fun removeAnswer(id: String, idx: Int) {
        _ui.value = _ui.value.copy(
            items = _ui.value.items.map { q ->
                if (q.id == id && q.answers.size > 2) {
                    val list = q.answers.toMutableList()
                    list.removeAt(idx)
                    val newCorrect = when {
                        q.correctIndex == null -> null
                        q.correctIndex!! == idx -> null
                        q.correctIndex!! > idx -> q.correctIndex!! - 1
                        else -> q.correctIndex
                    }
                    q.copy(answers = list, correctIndex = newCorrect)
                } else q
            }
        )
    }

    fun setCorrect(id: String, idx: Int) {
        _ui.value = _ui.value.copy(
            items = _ui.value.items.map { q ->
                if (q.id == id) q.copy(correctIndex = idx) else q
            }
        )
    }

    fun canFinish(): Boolean {
        return _ui.value.items.any() && _ui.value.items.all { q ->
            q.question.isNotBlank() &&
                    q.answers.size >= 2 &&
                    q.answers.all { it.isNotBlank() } &&
                    q.correctIndex != null &&
                    q.correctIndex!! in q.answers.indices
        }
    }
}
