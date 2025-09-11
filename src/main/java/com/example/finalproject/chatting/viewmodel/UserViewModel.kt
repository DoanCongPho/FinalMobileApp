package com.example.finalproject.chatting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.chatting.data.UserRepository
import com.example.finalproject.chatting.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val username: String, private val repo: UserRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            val result = repo.getUserByUsername(username)
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
            }
        }
    }
}
