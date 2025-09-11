package com.example.finalproject.chatting.data

import com.example.finalproject.chatting.model.Conversation
import com.example.finalproject.chatting.model.CreateConversationRequest


import com.example.finalproject.chatting.model.Participant
import com.example.finalproject.chatting.model.UpdateConversationRequest
import com.example.finalproject.core.network.api.chat.ConversationApi
import retrofit2.Response

class ConversationRepository(private val api: ConversationApi) {

    suspend fun getMyConversations(): List<Conversation> {
        return api.getMyConversations()
    }

//    suspend fun createConversation(recipientId: Int): Conversation {
//        val request = CreateConversationRequest(recipient_id = recipientId)
//        return api.createConversation(request)
//    }

    suspend fun createDirectConversation(recipientId: Int): Conversation {
        val request = CreateConversationRequest(
            recipient_id = recipientId,
            type = "direct"
        )
        return api.createConversation(request)
    }

    suspend fun createGroupConversation(
        name: String,
        description: String?,
        participantIds: List<Int>
    ): Conversation {
        val request = CreateConversationRequest(
            recipient_ids = participantIds,
            name = name,
            description = description,
            type = "group"
        )
        return api.createConversation(request)
    }

    suspend fun getConversation(id: Int): Result<Conversation> {
        return try {
            val res = api.getConversation(id)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteConversation(id: Int): Result<Unit> {
        return try {
            val res: Response<Unit> = api.deleteConversation(id)
            if (res.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Failed with code ${res.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateConversation(id: Int, name: String?, description: String?): Result<Conversation> {
        return try {
            val request = UpdateConversationRequest(name = name, description = description)
            val res = api.updateConversation(id, request)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun addOrUpdateParticipant(conversationId: Int, participant: Participant): Result<Participant> {
        return try {
            val res = api.addOrUpdateParticipant(conversationId, participant.user.id, participant)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeParticipant(conversationId: Int, userId: Int): Result<Participant> {
        return try {
            val res = api.removeParticipant(conversationId, userId)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun leaveConversation(conversationId: Int): Result<Participant> {
        return try {
            val res = api.leaveConversation(conversationId)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
