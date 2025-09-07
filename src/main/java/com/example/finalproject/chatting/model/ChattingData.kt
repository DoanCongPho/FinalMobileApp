package com.example.finalproject.chatting.model




data class User(
    val userId: String = "",
    val name: String = "",
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis()
)


//data class Chat(
//    val chatId: String = "",
//    val participants: List<String> = emptyList(), // userIds
//    val isGroup: Boolean = false,
//    val groupName: String? = null,
//    val groupAvatarUrl: String? = null,
//    val lastMessage: String? = null,
//    val lastMessageTime: Long = 0L
//)

data class Chat(
    val chatId: String,
    val name: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val avatarUrl: String? = null,
    val isMuted: Boolean = false,
    val isUnread: Boolean = false
)


enum class MessageType {
    TEXT, IMAGE, FILE, VOICE
}

data class Message(
    val messageId: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val content: String = "",   // message text OR file name
    val messageType: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,   // for image/file/voice
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isSendByMe: Boolean = false // 👈 UI alignment helper
)
