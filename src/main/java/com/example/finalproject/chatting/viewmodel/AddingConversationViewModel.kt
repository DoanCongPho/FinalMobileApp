package com.example.finalproject.chatting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.chatting.data.UserRepository
import com.example.finalproject.chatting.model.Conversation
import com.example.finalproject.chatting.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddConversationViewModel(
    private val userRepo: UserRepository,
    private val conversationViewModel: ConversationViewModel
) : ViewModel() {

    private val _recipientId = MutableStateFlow<Int?>(null)
    val recipientId: StateFlow<Int?> = _recipientId

    private val _foundUser = MutableStateFlow<User?>(null) // store user info
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

//    fun createConversation(onCreated: (Conversation) -> Unit = {}) {
//        val id = _recipientId.value ?: run {
//            _error.value = "No recipient selected"
//            return
//        }
//
//        // Delegate creation to the main ConversationViewModel
//        conversationViewModel.createConversation(id) { convo ->
//            onCreated(convo) // callback for navigation or updating UI
//        }
//    }

    fun createDirectConversation(onCreated: (Conversation) -> Unit) {
        val user = _foundUser.value
        if (user == null) {
            _error.value = "No recipient selected"
            return
        }

        // Delegate to ConversationViewModel with callback
        conversationViewModel.createDirectConversation(user.id) { convo ->
            onCreated(convo)   // navigate to the conversation screen
        }
    }

    fun createGroupConversation(
        name: String,
        participantIds: List<Int>,
        description: String? = null,
        onCreated: (Conversation) -> Unit
    ) {
        if (name.isBlank()) {
            _error.value = "Group name is required"
            return
        }

        conversationViewModel.createGroupConversation(name, participantIds, description) { convo ->
            onCreated(convo)  // navigate to the new group conversation
        }
    }

}
