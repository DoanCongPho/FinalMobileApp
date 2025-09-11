package com.example.finalproject.chatting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.chatting.data.UserRepository
import com.example.finalproject.chatting.model.Conversation
import com.example.finalproject.chatting.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddConversationChatRoomViewModel(
    private val userRepo: UserRepository,
    private val chatRoomManager: ChatRoomManagerViewModel
) : ViewModel() {

    private val _recipientId = MutableStateFlow<Int?>(null)
    val recipientId: StateFlow<Int?> = _recipientId

    private val _foundUser = MutableStateFlow<User?>(null)
    val foundUser: StateFlow<User?> = _foundUser

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _info = MutableStateFlow<String?>(null)
    val info: StateFlow<String?> = _info

    fun findUser(username: String) {
        viewModelScope.launch {
            val result = userRepo.getUserByUsername(username)
            if (result.isSuccess) {
                val user = result.getOrNull()
                _recipientId.value = user?.id
                _foundUser.value = user
                _error.value = null
            } else {
                _recipientId.value = null
                _foundUser.value = null
                _error.value = "User not found"
            }
        }
    }

    fun createDirectChat(onCreated: (Conversation) -> Unit) {
        val user = _foundUser.value
        if (user == null) {
            _error.value = "No recipient selected"
            return
        }
        // Use ChatRoomManagerViewModel to create a conversation
        chatRoomManager.createDirectConversation(user.id) { convo ->
            onCreated(convo)
        }
    }

    fun createGroupChat(
        name: String,
        participantIds: List<Int>,
        description: String? = null,
        onCreated: (Conversation) -> Unit
    ) {
        if (name.isBlank()) {
            _error.value = "Group name is required"
            return
        }
        chatRoomManager.createGroupConversation(name, participantIds, description) { convo ->
            onCreated(convo)
        }
    }
}
