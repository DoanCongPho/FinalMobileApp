// review/viewmodel/ReviewViewModel.kt
package com.example.finalproject.review.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.review.data.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReviewUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val recent: com.example.finalproject.review.model.RecentQuiz? = null,
    val items: List<com.example.finalproject.review.model.ReviewItem> = emptyList()
)

class ReviewViewModel(
    private val repo: ReviewRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ReviewUiState())
    val ui = _ui.asStateFlow()

    init { refresh() }

    fun refresh() {
        _ui.value = _ui.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching {
                val recent = repo.loadRecent()
                val list = repo.loadAvailable()
                _ui.value.copy(loading = false, recent = recent, items = list)
            }.onSuccess { _ui.value = it }
                .onFailure { e -> _ui.value = _ui.value.copy(loading = false, error = e.message) }
        }
    }

    fun removeItem(id: String) {
        // optimistic update
        val before = _ui.value.items
        _ui.value = _ui.value.copy(items = before.filterNot { it.id == id })
        viewModelScope.launch {
            runCatching { repo.deleteItem(id) }
                .onFailure { err ->
                    // rollback on failure
                    _ui.value = _ui.value.copy(items = before, error = err.message)
                }
        }
    }
}
