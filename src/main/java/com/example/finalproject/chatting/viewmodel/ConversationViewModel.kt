package com.example.finalproject.chatting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.chatting.data.ConversationRepository
import com.example.finalproject.chatting.data.MessageRepository
import com.example.finalproject.chatting.model.Conversation
import com.example.finalproject.chatting.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val conversationRepo: ConversationRepository,
    private val messageRepo: MessageRepository
) : ViewModel() {

    // Conversations state
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations

    // Messages map: conversationId -> messages
    private val _messagesMap = mutableMapOf<Int, MutableStateFlow<List<Message>>>()

    init {
        loadConversations()
    }

    // Load all conversations once
    private fun loadConversations() {
        viewModelScope.launch {
            try {
                _conversations.value = conversationRepo.getMyConversations()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Lazy fetch messages for a conversation
    fun getMessagesFor(convoId: Int): StateFlow<List<Message>> {
        val stateFlow = _messagesMap.getOrPut(convoId) { MutableStateFlow(emptyList()) }
        if (stateFlow.value.isEmpty()) {
            fetchMessages(convoId)
        }
        return stateFlow
    }


    private fun fetchMessages(convoId: Int) {
        viewModelScope.launch {
            try {
                val result = messageRepo.getMessages(convoId)
                val messages = result.getOrDefault(emptyList())
                _messagesMap.getOrPut(convoId) { MutableStateFlow(emptyList()) }.value = messages
            } catch (e: Exception) {
                _messagesMap.getOrPut(convoId) { MutableStateFlow(emptyList()) }.value = emptyList()
            }
        }
    }

//    fun createConversation(recipientId: Int, onSuccess: (Conversation) -> Unit = {}) {
//        viewModelScope.launch {
//            try {
//                val convo = conversationRepo.createConversation(recipientId)
//
//                // Only add if it doesn't already exist
//                val exists = _conversations.value.any { it.id == convo.id }
//                if (!exists) {
//                    _conversations.value = _conversations.value + convo
//                    fetchMessages(convo.id)
//                }
//
//                onSuccess(convo)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
fun createDirectConversation(recipientId: Int, onSuccess: (Conversation) -> Unit) {
    viewModelScope.launch {
        try {
            val existing = _conversations.value.find { convo ->
                convo.participants.any { it.user.id == recipientId } && convo.type == "direct"
            }

            val convo = if (existing != null) {
                existing
            } else {
                val newConvo = conversationRepo.createDirectConversation(recipientId)
                _conversations.value = _conversations.value + newConvo
                newConvo
            }

            onSuccess(convo) // callback to navigate or update UI

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

    fun createGroupConversation(
        name: String,
        participantIds: List<Int>,
        description: String? = null,
        onSuccess: (Conversation) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val convo = conversationRepo.createGroupConversation(name, description, participantIds)
                _conversations.value = _conversations.value + convo
                onSuccess(convo) // callback to navigate or update UI
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun deleteConversation(id: Int) {
        viewModelScope.launch {
            val result = conversationRepo.deleteConversation(id)
            if (result.isSuccess) {
                _conversations.value = _conversations.value.filter { it.id != id }
                _messagesMap.remove(id)
            }
        }
    }

    fun sendMessage(convoId: Int, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            try {
                val result = messageRepo.sendMessage(convoId, content)
                if (result.isSuccess) {
                    // Lấy message mới
                    val newMessage = result.getOrThrow()
                    // Cập nhật stateFlow cho conversation này
                    val stateFlow = _messagesMap.getOrPut(convoId) { MutableStateFlow(emptyList()) }
                    stateFlow.value = listOf(newMessage) + stateFlow.value
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
