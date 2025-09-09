package com.example.finalproject.chatting.model




data class User(
    val userId: String = "",
    val name: String = "",
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis(),
    val chatIds: List<String> = emptyList()
)

data class Chat(
    val chatId: String,
    val name: String?,
    val lastMessage: String,
    val lastMessageTime: String,
//    val avatarUrl: String? = null,
//    val isMuted: Boolean = false,
//    val isUnread: Boolean = false,
    val participants: List<String> = emptyList(),
)
enum class MessageType {
    TEXT, IMAGE, FILE, VOICE
}
data class Message(
    val messageId: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",   // message text OR file name
    val messageType: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,   // for image/file/voice
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isSendByMe: Boolean = false // 👈 UI alignment helper
)
