package com.example.finalproject.chatting.data

import com.example.finalproject.chatting.model.Chat
import com.example.finalproject.chatting.model.Message
import com.example.finalproject.chatting.model.MessageType
import com.example.finalproject.chatting.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object ChatRepository {
    private val chats = MutableStateFlow(
        listOf(
            Chat(
                chatId = "1",
                name = "",
                lastMessage = "Hello! I’m your AI assistant 🤖",
                lastMessageTime = "11/19/19",
                participants = listOf("congpho123", "ai")
            ),
            Chat(
                chatId = "2",
                name = "",
                lastMessage = "What kind of strategy is better?",
                lastMessageTime = "11/16/19",
                participants = listOf("congpho123", "andrew")
            ),
            Chat(
                chatId = "3",
                name = "",
                lastMessage = "🎤 Voice message (0:14)",
                lastMessageTime = "11/15/19",
                participants = listOf("congpho123", "karen")
            ),
            Chat(
                chatId = "4",
                name = "",
                lastMessage = "Bro, I have a good idea!",
                lastMessageTime = "10/30/19",
                participants = listOf("congpho123", "max")
            ),
            Chat(
                chatId = "5",
                name = "",
                lastMessage = "📷 Photo",
                lastMessageTime = "10/28/19",
                participants = listOf("congpho123", "martha")
            ),
            Chat(
                chatId = "6",
                name = "Secret Chat",
                lastMessage = "Welcome!",
                lastMessageTime = "10/25/19",
                participants = listOf("congpho123", "ai", "andrew", "karen", "max", "martha")
            )
        )
    )

    // Trả về flow để UI có thể observe
    fun getChatsFlow(): StateFlow<List<Chat>> = chats

    // Lấy danh sách chat hiện tại (snapshot)
    fun getChats(): List<Chat> = chats.value

    fun getChatsByUser(userId: String): List<Chat> =
        chats.value.filter { userId in it.participants }

    fun getChatsByIds(chatIds: List<String>): List<Chat> =
        chats.value.filter { it.chatId in chatIds }

    // Thêm chat mới
    fun addChat(chat: Chat) {
        chats.value = chats.value + chat // tạo list mới
    }
    // Cập nhật toàn bộ chat
    fun updateChat(updatedChat: Chat) {
        val updatedChats = chats.value.map { chat ->
            if (chat.chatId == updatedChat.chatId) updatedChat else chat
        }
        chats.value = updatedChats
    }
}




object MessageRepository {
    private val messages = mutableListOf(
        // Chat 1: AI Chatbox
        Message(
            messageId = "1",
            chatId = "1",
            senderId = "ai",
            content = "Hello! How can I help you today? 😊",
            messageType = MessageType.TEXT,
            timestamp = 1694041200000,
            isSendByMe = false
        ),
        Message(
            messageId = "2",
            chatId = "1",
            senderId = "congpho123",
            content = "Hi! Can you tell me about Kotlin?",
            messageType = MessageType.TEXT,
            timestamp = 1694041500000,
            isSendByMe = true
        ),
        Message(
            messageId = "3",
            chatId = "1",
            senderId = "ai",
            content = "Sure! Kotlin is a modern programming language that runs on the JVM.",
            messageType = MessageType.TEXT,
            timestamp = 1694041800000,
            isSendByMe = false
        ),

        // Chat 2: Andrew
        Message(
            messageId = "1",
            chatId = "2",
            senderId = "andrew",
            content = "What kind of strategy is better?",
            messageType = MessageType.TEXT,
            timestamp = 1693954800000,
            isSendByMe = false
        ),
        Message(
            messageId = "2",
            chatId = "2",
            senderId = "congpho123",
            content = "I think a balanced one is best.",
            messageType = MessageType.TEXT,
            timestamp = 1693955100000,
            isSendByMe = true
        ),

        // Chat 3: Karen
        Message(
            messageId = "1",
            chatId = "3",
            senderId = "karen",
            content = "🎤 Voice message (0:14)",
            messageType = MessageType.VOICE,
            mediaUrl = "https://example.com/voice/14s.mp3",
            timestamp = 1693868400000,
            isSendByMe = false
        ),
        Message(
            messageId = "2",
            chatId = "3",
            senderId = "congpho123",
            content = "Nice voice!",
            messageType = MessageType.TEXT,
            timestamp = 1693868700000,
            isSendByMe = true
        ),

        // Chat 4: Max
        Message(
            messageId = "1",
            chatId = "4",
            senderId = "max",
            content = "Bro, I have a good idea!",
            messageType = MessageType.TEXT,
            timestamp = 1693782000000,
            isSendByMe = false
        ),
        Message(
            messageId = "2",
            chatId = "4",
            senderId = "congpho123",
            content = "Tell me more 🤔",
            messageType = MessageType.TEXT,
            timestamp = 1693782300000,
            isSendByMe = true
        ),

        // Chat 5: Martha
        Message(
            messageId = "1",
            chatId = "5",
            senderId = "martha",
            content = "📷 Photo",
            messageType = MessageType.IMAGE,
            mediaUrl = "https://picsum.photos/200/300",
            timestamp = 1693695600000,
            isSendByMe = false
        ),
        Message(
            messageId = "2",
            chatId = "5",
            senderId = "congpho123",
            content = "Nice picture 😍",
            messageType = MessageType.TEXT,
            timestamp = 1693695900000,
            isSendByMe = true
        ),

        // Chat 6: Group chat
        Message(
            messageId = "1",
            chatId = "6",
            senderId = "ai",
            content = "Welcome everyone to the group chat 🎉",
            messageType = MessageType.TEXT,
            timestamp = 1693609200000,
            isSendByMe = false
        ),
        Message(
            messageId = "2",
            chatId = "6",
            senderId = "andrew",
            content = "Hi all 👋",
            messageType = MessageType.TEXT,
            timestamp = 1693609500000,
            isSendByMe = false
        ),
        Message(
            messageId = "3",
            chatId = "6",
            senderId = "congpho123",
            content = "Glad to see you guys here 😎",
            messageType = MessageType.TEXT,
            timestamp = 1693609800000,
            isSendByMe = true
        )
    )

    fun getMessagesForChat(chatId: String): List<Message> {
        return messages.filter { it.chatId == chatId }.sortedByDescending { it.timestamp }
    }

    fun addMessage(message: Message) {
        messages.add(message)
    }

    fun generateMessageId(chatId: String): String {
        val chatMessages = getMessagesForChat(chatId)
        val lastId = chatMessages.maxOfOrNull { it.messageId.toIntOrNull() ?: 0 } ?: 0
        return (lastId + 1).toString()
    }

    fun updateLastMessage(chatId: String, message: Message) {
        val chatIndex = ChatRepository.getChats().indexOfFirst { it.chatId == chatId}
        if (chatIndex != -1) {
            val chat = ChatRepository.getChats()[chatIndex]
            val updatedChat = chat.copy(
                lastMessage = when (message.messageType) {
                    MessageType.TEXT -> message.content
                    MessageType.IMAGE -> "📷 Photo"
                    MessageType.FILE -> "📎 File"
                    MessageType.VOICE -> "🎤 Voice message"
                },
                lastMessageTime = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
                    .format(Date(message.timestamp))
            )
            ChatRepository.updateChat(updatedChat)
        }
    }
}


object UserRepository {
    // Mock users
    private val users = mutableListOf(
        User(
            userId = "congpho123",
            name = "Pho",
            avatarUrl = "https://i.pravatar.cc/150?img=1",
            isOnline = true,
            lastSeen = System.currentTimeMillis(),
            chatIds = listOf("1", "2", "3", "4", "5", "6")
        ),
        User(
            userId = "ai",
            name = "AI Chatbox",
            avatarUrl = "https://cdn-icons-png.flaticon.com/512/4712/4712107.png",
            isOnline = true,
            lastSeen = System.currentTimeMillis(),
            chatIds = listOf("1")
        ),
        User(
            userId = "andrew",
            name = "Andrew Parker",
            avatarUrl = "https://i.pravatar.cc/150?img=2",
            isOnline = false,
            lastSeen = System.currentTimeMillis() - 1000 * 60 * 15,
            chatIds = listOf("2")
        ),
        User(
            userId = "karen",
            name = "Karen Castillo",
            avatarUrl = "https://i.pravatar.cc/150?img=3",
            isOnline = true,
            lastSeen = System.currentTimeMillis(),
            chatIds = listOf("4")
        ),
        User(
            userId = "max",
            name = "Maximillian Jacobson",
            avatarUrl = "https://i.pravatar.cc/150?img=4",
            isOnline = false,
            lastSeen = System.currentTimeMillis() - 1000 * 60 * 60, // 1h ago
            chatIds = listOf("5")
        ),
        User(
            userId = "martha",
            name = "Martha Craig",
            avatarUrl = "https://i.pravatar.cc/150?img=5",
            isOnline = false,
            lastSeen = System.currentTimeMillis() - 1000 * 60 * 60 * 5,
            chatIds = listOf("6")
        )
    )

    fun getUserById(userId: String): User? = users.find { it.userId == userId }

    fun getChatIdsForUser(userId: String): List<String> {
        return getUserById(userId)?.chatIds ?: emptyList()
    }

    fun getAllParticipantsForUser(userId: String): List<User> {
        // lấy danh sách chat mà user này tham gia
        val chatIds = UserRepository.getChatIdsForUser(userId)

        // gom tất cả participantId từ các chat này
        val participantIds = ChatRepository.getChats()
            .filter { it.chatId in chatIds }
            .flatMap { it.participants }
            .distinct() // tránh trùng lặp

        // trả về list User tương ứng, bỏ user chính
        return participantIds
            .filter { it != userId }
            .mapNotNull { UserRepository.getUserById(it) }
    }

    fun updateUser(userId: String, updatedUser: User) {
        val index = users.indexOfFirst { it.userId == userId }
        if (index != -1) {
            users[index] = updatedUser
        }
    }
}
