package com.example.finalproject.core.network.api.chat

import com.example.finalproject.chatting.model.Conversation
import com.example.finalproject.chatting.model.CreateConversationRequest

import com.example.finalproject.chatting.model.Participant
import com.example.finalproject.chatting.model.UpdateConversationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path



interface ConversationApi {
    @GET("users/me/conversations")
    suspend fun getMyConversations(): List<Conversation>

    @GET("conversations/{conversation_id}")
    suspend fun getConversation(
        @Path("conversation_id") id: Int
    ): Conversation

    @POST("users/me/conversations")
    suspend fun createConversation(
        @Body request: CreateConversationRequest
    ): Conversation

    @DELETE("conversations/{conversation_id}")
    suspend fun deleteConversation(@Path("conversation_id") id: Int): Response<Unit>

    @PATCH("conversations/{conversation_id}")
    suspend fun updateConversation(
        @Path("conversation_id") id: Int,
        @Body request: UpdateConversationRequest
    ): Conversation

    @PUT("conversations/{conversation_id}/participants/{user_id}")
    suspend fun addParticipant(
        @Path("conversation_id") conversationId: Int,
        @Path("user_id") userId: Int,
    ): Participant

    // Remove participant
    @DELETE("conversations/{conversation_id}/participants/{user_id}")
    suspend fun removeParticipant(
        @Path("conversation_id") conversationId: Int,
        @Path("user_id") userId: Int
    ): Participant

    // Leave conversation (current user)
    @DELETE("conversations/{conversation_id}/participants/me")
    suspend fun leaveConversation(
        @Path("conversation_id") conversationId: Int
    ): Participant
}
