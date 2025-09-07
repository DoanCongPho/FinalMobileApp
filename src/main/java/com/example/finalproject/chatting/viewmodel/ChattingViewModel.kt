package com.example.finalproject.chatting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finalproject.chatting.data.ChatRepository
import com.example.finalproject.chatting.data.MessageRepository
import com.example.finalproject.chatting.model.Chat
import com.example.finalproject.chatting.model.Message
import com.example.finalproject.chatting.model.MessageType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



class ChatListViewModel : ViewModel() {
    private val _chats = MutableStateFlow(ChatRepository.getChats())
    val chats: StateFlow<List<Chat>> = _chats

    fun addChat(chat: Chat) {
        ChatRepository.addChat(chat)
        _chats.value = ChatRepository.getChats()
    }
}


class MessageViewModel(private val chatId: String) : ViewModel() {

    // MutableStateFlow để quản lý danh sách tin nhắn
    private val _messages = MutableStateFlow(MessageRepository.getMessagesForChat(chatId))
    val messages: StateFlow<List<Message>> = _messages

    // Hàm thêm tin nhắn mới
    fun sendMessage(content: String) {
        viewModelScope.launch {
            val newMessage = Message(
                messageId = MessageRepository.generateMessageId(chatId),
                chatId = chatId,
                senderId = "me", // hoặc lấy từ UserSession
                content = content,
                messageType = MessageType.TEXT,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                isSendByMe = true
            )
            MessageRepository.addMessage(newMessage)
            MessageRepository.updateLastMessage(chatId, newMessage)
            _messages.value = MessageRepository.getMessagesForChat(chatId)
        }
    }


    // Hàm load lại tin nhắn (nếu cần)
    fun refreshMessages() {
        _messages.value = MessageRepository.getMessagesForChat(chatId)
    }
}

class MessageViewModelFactory(private val chatId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageViewModel(chatId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
