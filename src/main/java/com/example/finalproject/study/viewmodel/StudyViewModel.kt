package com.example.finalproject.study.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.auth.login.model.UserProfile
import com.example.finalproject.core.network.api.auth.LoginApi
import com.example.finalproject.study.data.StudyRepository
import com.example.finalproject.study.model.Quote
import com.example.finalproject.study.model.Topic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

data class StudyUiState(
    val displayName: String = "TEST NAME", // Very obvious test name
    val greeting: String = "TEST GREETING", // Very obvious test greeting
    val userProfile: UserProfile? = null,
    val quote: Quote? = null,
    val topics: List<Topic> = emptyList(),
    val selectedTopicIds: Set<String> = emptySet(),
    val isLoadingUser: Boolean = false
)

class StudyViewModel(
    private val repo: StudyRepository,
    private val loginApi: LoginApi
) : ViewModel() {

    private val _ui = MutableStateFlow(StudyUiState())
    val ui = _ui.asStateFlow()

    init {
        // Set the time-based greeting immediately
        val greeting = getTimeBasedGreeting()
        _ui.value = _ui.value.copy(
            greeting = "INIT GREETING: $greeting",
            displayName = "INIT NAME: Endy"
        )
        refresh()
        loadUserProfile()
    }

    private fun getTimeBasedGreeting(): String {
        val currentHour = LocalTime.now().hour
        return when (currentHour) {
            in 5..11 -> "GOOD MORNING"
            in 12..17 -> "GOOD AFTERNOON"
            in 18..21 -> "GOOD EVENING"
            else -> "GOOD NIGHT"
        }
    }

    private fun loadUserProfile() = viewModelScope.launch {
        _ui.value = _ui.value.copy(isLoadingUser = true)
        try {
            println("StudyViewModel: Attempting to load user profile...")
            val response = loginApi.getCurrentUser()
            println("StudyViewModel: API response received, isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val userProfile = response.body()
                println("StudyViewModel: User profile loaded: ${userProfile?.name}")
                _ui.value = _ui.value.copy(
                    userProfile = userProfile,
                    displayName = userProfile?.name ?: "User",
                    greeting = getTimeBasedGreeting(),
                    isLoadingUser = false
                )
            } else {
                println("StudyViewModel: API call failed with code: ${response.code()}")
                _ui.value = _ui.value.copy(
                    greeting = getTimeBasedGreeting(),
                    displayName = "User", // Keep default name
                    isLoadingUser = false
                )
            }
        } catch (e: Exception) {
            println("StudyViewModel: Exception loading user profile: ${e.message}")
            _ui.value = _ui.value.copy(
                greeting = getTimeBasedGreeting(),
                displayName = "User", // Keep default name
                isLoadingUser = false
            )
        }
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
