package com.example.finalproject.chatting.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.chatting.model.*
import com.example.finalproject.chatting.websocket.GatewaySocket
import com.example.finalproject.core.DataStore.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ChatViewModel(context: Context) : ViewModel() {

    private val tokenManager = TokenManager.getInstance(context)

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations

    private var gatewaySocket: GatewaySocket? = null

    init {
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
        when (eventType) {
            "MESSAGE_CREATE" -> {
                val msg = parseMessage(data)
                _messages.value = _messages.value + msg
            }
            "MESSAGE_DELETE" -> {
                val id = data.getInt("id")
                _messages.value = _messages.value.filter { it.id != id }
            }
            "CONVERSATION_CREATE", "CONVERSATION_UPDATE" -> {
                val convo = parseConversation(data)
                val exists = _conversations.value.any { it.id == convo.id }
                if (!exists) {
                    _conversations.value = _conversations.value + convo
                }
            }
            "CONVERSATION_DELETE" -> {
                val id = data.getInt("id")
                _conversations.value = _conversations.value.filter { it.id != id }
            }
            "CONVERSATION_PARTICIPANTS_UPDATE" -> {
                val convoId = data.getInt("id")
                val added = data.optJSONArray("added_participants") ?: JSONArray()
                val removed = data.optJSONArray("removed_participant_ids") ?: JSONArray()

                val updated = _conversations.value.map { c ->
                    if (c.id == convoId) {
                        val current = c.participants.toMutableList()
                        for (i in 0 until added.length()) {
                            val p = added.getJSONObject(i)
                            val user = p.getJSONObject("user")
                            current.add(
                                Participant(
                                    user = User(
                                        id = user.getInt("id"),
                                        name = user.getString("name"),
                                        createdAt = user.getString("created_at"),
                                        updatedAt = user.getString("updated_at")
                                    ),
                                    role = p.getString("role"),
                                    joinedAt = p.getString("joined_at")
                                )
                            )
                        }
                        for (i in 0 until removed.length()) {
                            val uid = removed.getInt(i)
                            current.removeAll { it.user.id == uid }
                        }
                        c.copy(participants = current)
                    } else c
                }
                _conversations.value = updated
            }
        }
    }

    private fun parseMessage(data: JSONObject): Message {
        val attachments = mutableListOf<Attachment>()
        val attArray = data.optJSONArray("attachments") ?: JSONArray()
        for (i in 0 until attArray.length()) {
            val a = attArray.getJSONObject(i)
            attachments.add(
                Attachment(
                    id = a.getInt("id"),
                    filename = a.getString("filename"),
                    content_type = a.getString("content_type"),
                    file_size = a.getInt("file_size"),
                    url = a.optString("url", null)
                )
            )
        }

        return Message(
            id = data.getInt("id"),
            conversationId = data.getInt("conversation_id"),
            replyToId = if (data.isNull("reply_to_id")) null else data.getInt("reply_to_id"),
            userId = data.getInt("user_id"),
            content = data.optString("content", null),
            createdAt = data.getString("created_at"),
            updatedAt = data.getString("updated_at"),
            editedAt = if (data.isNull("edited_at")) null else data.getString("edited_at"),
            attachments = attachments
        )
    }

    private fun parseConversation(data: JSONObject): Conversation {
        val participantsJson = data.optJSONArray("participants") ?: JSONArray()
        val participants = mutableListOf<Participant>()
        for (i in 0 until participantsJson.length()) {
            val p = participantsJson.getJSONObject(i)
            val u = p.getJSONObject("user")
            participants.add(
                Participant(
                    user = User(
                        id = u.getInt("id"),
                        name = u.getString("name"),
                        createdAt = u.getString("created_at"),
                        updatedAt = u.getString("updated_at")
                    ),
                    role = p.getString("role"),
                    joinedAt = p.getString("joined_at")
                )
            )
        }

        return Conversation(
            id = data.getInt("id"),
            type = data.getString("type"),
            name = data.optString("name", ""),
            description = data.optString("description", null),
            createdAt = data.getString("created_at"),
            updatedAt = if (data.isNull("updated_at")) null else data.getString("updated_at"),
            requireMemberApproval = data.getBoolean("require_member_approval"),
            participants = participants
        )
    }

    fun sendMessage(message: String, conversationId: Int) {
        // TODO: Gọi REST API gửi message
        val tempMsg = Message(
            id = -1,
            conversationId = conversationId,
            replyToId = null,
            userId = -1,
            content = message,
            createdAt = "",
            updatedAt = "",
            editedAt = null,
            attachments = emptyList()
        )
        _messages.value = _messages.value + tempMsg
    }

    override fun onCleared() {
        super.onCleared()
        gatewaySocket?.disconnect()
    }
}
