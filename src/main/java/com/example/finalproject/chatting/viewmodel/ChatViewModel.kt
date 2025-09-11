package com.example.finalproject.chatting.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.chatting.data.ConversationRepository
import com.example.finalproject.chatting.data.MessageRepository
import com.example.finalproject.chatting.model.*
import com.example.finalproject.chatting.websocket.GatewaySocket
import com.example.finalproject.core.DataStore.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import org.json.JSONObject
class ChatRoomManagerViewModel(
    private val conversationRepo: ConversationRepository,
    private val messageRepo: MessageRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId: StateFlow<Int?> = _currentUserId

    // Conversations state
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations

    // Messages map: conversationId -> messages
    private val _messagesMap = mutableMapOf<Int, MutableStateFlow<List<Message>>>()

    // WebSocket
    private var gatewaySocket: GatewaySocket? = null

    init {
        loadConversations()
        initWebSocket()
        viewModelScope.launch {
            tokenManager.userId.collect { id ->
                _currentUserId.value = id
            }
        }
    }

    private fun loadConversations() {
        viewModelScope.launch {
            try {
                _conversations.value = conversationRepo.getMyConversations()
                // Load messages cho tất cả conversation
                _conversations.value.forEach { convo ->
                    fetchMessages(convo.id)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun fetchMessages(convoId: Int) {
        viewModelScope.launch {
            try {
                val msgs = messageRepo.getMessages(convoId).getOrDefault(emptyList())
                _messagesMap.getOrPut(convoId) { MutableStateFlow(emptyList()) }.value = msgs
            } catch (e: Exception) {
                _messagesMap.getOrPut(convoId) { MutableStateFlow(emptyList()) }.value = emptyList()
            }
        }
    }

    fun getMessagesFor(convoId: Int): StateFlow<List<Message>> {
        return _messagesMap.getOrPut(convoId) { MutableStateFlow(emptyList()) }
    }

    private fun initWebSocket() {
        viewModelScope.launch {
            tokenManager.accessToken.collect { token ->
                token?.let {
                    if (gatewaySocket == null) {
                        gatewaySocket = GatewaySocket(it) { eventType, data ->
                            handleEvent(eventType, data)
                        }
                        gatewaySocket?.connect()
                    }
                }
            }
        }
    }

    private fun handleEvent(eventType: String, data: JSONObject) {
        viewModelScope.launch {
            when (eventType) {
                "MESSAGE_CREATE" -> {
                    val msg = parseMessage(data)

                    if (msg.userId != currentUserId.value) {
                        val stateFlow = _messagesMap.getOrPut(msg.conversationId) { MutableStateFlow(emptyList()) }
                        stateFlow.value = listOf(msg) + stateFlow.value
                    }
                }
                "MESSAGE_DELETE" -> {
                    val id = data.getInt("id")
                    _messagesMap.values.forEach { stateFlow ->
                        stateFlow.value = stateFlow.value.filter { it.id != id }
                    }
                }
                "CONVERSATION_CREATE", "CONVERSATION_UPDATE" -> {
                    val convo = parseConversation(data)
                    val exists = _conversations.value.any { it.id == convo.id }
                    if (!exists) _conversations.value = _conversations.value + convo
                }
                "CONVERSATION_DELETE" -> {
                    val id = data.getInt("id")
                    _conversations.value = _conversations.value.filter { it.id != id }
                    _messagesMap.remove(id)
                }
            }
        }
    }

    private fun parseMessage(data: JSONObject): Message {
        // parse message từ JSON
        return Message(
            id = data.getInt("id"),
            conversationId = data.getInt("conversation_id"),
            replyToId = if (data.isNull("reply_to_id")) null else data.getInt("reply_to_id"),
            userId = data.getInt("user_id"),
            content = data.optString("content", null),
            createdAt = data.getString("created_at"),
            updatedAt = data.getString("updated_at"),
            editedAt = if (data.isNull("edited_at")) null else data.getString("edited_at"),
            attachments = emptyList()
        )
    }

    private fun parseConversation(data: JSONObject): Conversation {
        // parse conversation từ JSON
        return Conversation(
            id = data.getInt("id"),
            type = data.getString("type"),
            name = data.optString("name", ""),
            description = data.optString("description", null),
            createdAt = data.getString("created_at"),
            updatedAt = if (data.isNull("updated_at")) null else data.getString("updated_at"),
            requireMemberApproval = data.getBoolean("require_member_approval"),
            participants = emptyList()
        )
    }

    fun sendMessage(convoId: Int, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            try {
                val result = messageRepo.sendMessage(convoId, content)
                if (result.isSuccess) {
                    val newMsg = result.getOrThrow()
                    val stateFlow = _messagesMap.getOrPut(convoId) { MutableStateFlow(emptyList()) }
                    stateFlow.value = listOf(newMsg) + stateFlow.value
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gatewaySocket?.disconnect()
    }


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

}

