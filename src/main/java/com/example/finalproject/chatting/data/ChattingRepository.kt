package com.example.finalproject.chatting.data

import com.example.finalproject.chatting.model.Chat
import com.example.finalproject.chatting.model.Message
import com.example.finalproject.chatting.model.MessageType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object ChatRepository {
    private val chats = mutableListOf(
        Chat("1", "AI Chatbox", "Hello! I’m your AI assistant 🤖", "11/19/19"),
        Chat("2", "Andrew Parker", "What kind of strategy is better?", "11/16/19"),
        Chat("3", "Karen Castillo", "🎤 Voice message (0:14)", "11/15/19"),
        Chat("4", "Maximillian Jacobson", "Bro, I have a good idea!", "10/30/19"),
        Chat("5", "Martha Craig", "📷 Photo", "10/28/19"),
//        Chat("6", "Tabitha Potter", "Actually I wanted to check with you...", "8/25/19"),
//        Chat("7", "Maisy Humphrey", "Welcome, to make design process faster...", "8/20/19"),
//        Chat("8", "Kieron Dotson", "Ok, have a good trip!", "7/29/19"),
//        Chat("9", "AI Chatbox", "Hello! I’m your AI assistant 🤖", "11/19/19"),
//        Chat("10", "Andrew Parker", "What kind of strategy is better?", "11/16/19"),
//        Chat("11", "Karen Castillo", "🎤 Voice message (0:14)", "11/15/19"),
//        Chat("12", "Maximillian Jacobson", "Bro, I have a good idea!", "10/30/19"),
//        Chat("13", "Martha Craig", "📷 Photo", "10/28/19"),
//        Chat("14", "Tabitha Potter", "Actually I wanted to check with you...", "8/25/19"),
//        Chat("15", "Maisy Humphrey", "Welcome, to make design process faster...", "8/20/19"),
//        Chat("16", "Kieron Dotson", "Ok, have a good trip!", "7/29/19")

    )

    fun getChats(): List<Chat> = chats

    fun addChat(chat: Chat) {
        chats.add(chat)
    }
}





object MessageRepository {
    private val messages = mutableListOf(
        Message(
            messageId = "1",
            chatId = "1",
            senderId = "ai",
            content = "Hello! How can I help you today? 😊",
            messageType = MessageType.TEXT,
            timestamp = 1694041200000, // 07/09/2023 10:00:00 GMT+7
            isSendByMe = false
        ),
        Message(
            messageId = "2",
            chatId = "1",
            senderId = "me",
            content = "Hi! Can you tell me about Kotlin?",
            messageType = MessageType.TEXT,
            timestamp = 1694041500000, // 07/09/2023 10:05:00 GMT+7
            isSendByMe = true
        ),
        Message(
            messageId = "3",
            chatId = "1",
            senderId = "ai",
            content = "Sure! Kotlin is a modern programming language that runs on the JVM.",
            messageType = MessageType.TEXT,
            timestamp = 1694041800000, // 07/09/2023 10:10:00 GMT+7
            isSendByMe = false
        ),
        Message(
            messageId = "4",
            chatId = "2",
            senderId = "andrew",
            content = "What kind of strategy is better?",
            messageType = MessageType.TEXT,
            timestamp = 1693954800000, // 06/09/2023 10:00:00 GMT+7
            isSendByMe = false
        ),
        Message(
            messageId = "5",
            chatId = "2",
            senderId = "me",
            content = "I think a balanced one is best.",
            messageType = MessageType.TEXT,
            timestamp = 1693955100000, // 06/09/2023 10:05:00 GMT+7
            isSendByMe = true
        )
    )

    fun getMessagesForChat(chatId: String): List<Message> {
        return messages.filter { it.chatId == chatId }.sortedByDescending{ it.timestamp }
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
        val chatIndex = ChatRepository.getChats().indexOfFirst { it.chatId == chatId }
        if (chatIndex != -1) {
            val chat = ChatRepository.getChats()[chatIndex]
            val updatedChat = chat.copy(
                lastMessage = when (message.messageType) {
                    MessageType.TEXT -> message.content
                    MessageType.IMAGE -> "📷 Photo"
                    MessageType.FILE -> "📎 File"
                    MessageType.VOICE -> "🎤 Voice message"
                },
                lastMessageTime = SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(Date(message.timestamp))
            )
            (ChatRepository.getChats() as MutableList)[chatIndex] = updatedChat
        }
    }

}
