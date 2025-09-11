package com.example.finalproject.chatting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.chatting.data.ConversationRepository
import com.example.finalproject.chatting.data.MessageRepository
import com.example.finalproject.chatting.data.UserRepository




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




class AddConversationViewModelFactory(
    private val userRepo: UserRepository,
    private val conversationViewModel: ConversationViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddConversationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddConversationViewModel(userRepo, conversationViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}