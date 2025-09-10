package com.example.finalproject.review.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.review.data.ReviewRepository

class ReviewViewModelFactory(
    private val repo: ReviewRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            return ReviewViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown VM: $modelClass")
    }
}
