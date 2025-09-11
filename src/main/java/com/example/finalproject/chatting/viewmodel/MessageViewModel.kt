package com.example.finalproject.chatting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.chatting.data.MessageRepository
import com.example.finalproject.chatting.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageViewModel(
    private val conversationId: Int,
    private val repo: MessageRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    init {
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            repo.getMessages(conversationId)
                .onSuccess { messages ->
                    _messages.value = messages
                }
                .onFailure { e ->
                    e.printStackTrace()
                    _messages.value = emptyList()
                }
        }
    }



    fun sendMessage(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val result = repo.sendMessage(conversationId, content)
            if (result.isSuccess) {
                _messages.value = listOf(result.getOrThrow()) + _messages.value
            }
        }
    }

    fun deleteMessage(messageId: Int) {
        viewModelScope.launch {
            val result = repo.deleteMessage(conversationId, messageId)
            if (result.isSuccess) {
                _messages.value = _messages.value.filter { it.id != messageId }
            }
        }
    }
}
