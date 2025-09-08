package com.example.finalproject.chatting.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finalproject.chatting.data.ChatRepository
import com.example.finalproject.chatting.data.MessageRepository
import com.example.finalproject.chatting.data.UserRepository
import com.example.finalproject.chatting.model.Chat
import com.example.finalproject.chatting.model.Message
import com.example.finalproject.chatting.model.MessageType
import com.example.finalproject.chatting.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MessageViewModel(private val chatId: String) : ViewModel() {

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
class ChatListViewModel(
    private val currentUserId: String
) : ViewModel() {

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    init {
        // Observe trực tiếp từ ChatRepository
        viewModelScope.launch {
            ChatRepository.getChatsFlow().collectLatest { allChats ->
                // Lọc ra các chat mà user hiện tại tham gia
                _chats.value = allChats.filter { currentUserId in it.participants }
            }
        }
    }

    fun getDisplayName(chat: Chat): String {
        return if (!chat.name.isNullOrBlank()) {
            chat.name!!
        } else {
            val otherId = chat.participants.firstOrNull { it != currentUserId }
            val otherUser = otherId?.let { UserRepository.getUserById(it) }
            otherUser?.name ?: "Unknown"
        }
    }
    fun getUrlAvatar(chat: Chat): String? {
        val otherId = chat.participants.firstOrNull { it != currentUserId }
        val otherUser = otherId?.let { UserRepository.getUserById(it) }
        return otherUser?.avatarUrl
    }
}


class UserViewModel(private val userId: String) : ViewModel() {
    private val _currentUser = MutableStateFlow(UserRepository.getUserById(userId))
    val currentUser: StateFlow<User?> = _currentUser

    private val _participants = MutableStateFlow(UserRepository.getAllParticipantsForUser(userId))
    val participants: StateFlow<List<User>> = _participants

    fun refresh() {
        _currentUser.value = UserRepository.getUserById(userId)
        _participants.value = UserRepository.getAllParticipantsForUser(userId)
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

class UserViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ChatListViewModelFactory(private val currentUserId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatListViewModel(currentUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}