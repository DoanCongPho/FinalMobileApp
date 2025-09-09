package com.example.finalproject.study.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.study.data.StudyRepository
import com.example.finalproject.study.model.Quote
import com.example.finalproject.study.model.Topic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudyUiState(
    val displayName: String = "Endy",
    val quote: Quote? = null,
    val topics: List<Topic> = emptyList(),
    val selectedTopicIds: Set<String> = emptySet()
)

class StudyViewModel(
    private val repo: StudyRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(StudyUiState())
    val ui = _ui.asStateFlow()

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        val quote = repo.loadQuote()
        val topics = repo.loadTopics()
        _ui.value = _ui.value.copy(quote = quote, topics = topics)
    }

    fun toggleTopic(id: String) {
        val now = _ui.value.selectedTopicIds.toMutableSet()
        if (!now.add(id)) now.remove(id)
        _ui.value = _ui.value.copy(selectedTopicIds = now)
    }
}
