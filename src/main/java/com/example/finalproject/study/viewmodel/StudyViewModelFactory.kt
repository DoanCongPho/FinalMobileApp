package com.example.finalproject.study.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.study.data.StudyRepository

class StudyViewModelFactory(
    private val repo: StudyRepository = StudyRepository()
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StudyViewModel(repo) as T
    }
}
