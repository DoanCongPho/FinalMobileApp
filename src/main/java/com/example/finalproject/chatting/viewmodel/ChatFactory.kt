package com.example.finalproject.chatting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.chatting.data.ConversationRepository
import com.example.finalproject.chatting.data.MessageRepository
import com.example.finalproject.chatting.data.UserRepository
import com.example.finalproject.core.DataStore.TokenManager


class ConversationViewModelFactory(
    private val conversationRepo: ConversationRepository,
    private val messageRepo: MessageRepository,

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConversationViewModel(conversationRepo, messageRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



class AddConversationChatRoomViewModelFactory(
    private val userRepo: UserRepository,
    private val chatRoomManager: ChatRoomManagerViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddConversationChatRoomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddConversationChatRoomViewModel(userRepo, chatRoomManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



class ChatRoomManagerViewModelFactory(
    private val conversationRepo: ConversationRepository,
    private val messageRepo: MessageRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatRoomManagerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatRoomManagerViewModel(conversationRepo, messageRepo, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
