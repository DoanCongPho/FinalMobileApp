package com.example.finalproject.core.network.api.chat

import com.example.finalproject.chatting.model.Message
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ConversationMessageApi {

    @GET("conversations/{conversation_id}/messages")
    suspend fun getMessages(
        @Path("conversation_id") conversationId: Int
    ): retrofit2.Response<List<Message>>

    @Multipart
    @POST("conversations/{conversation_id}/messages")
    suspend fun sendMessage(
        @Path("conversation_id") conversationId: Int,
        @Part("content") content: RequestBody,
        @Part("reply_to_id") replyToId: RequestBody?,
        @Part attachments: List<MultipartBody.Part>?
    ): Message


    @DELETE("conversations/{conversation_id}/messages/{message_id}")
    suspend fun deleteMessage(
        @Path("conversation_id") conversationId: Int,
        @Path("message_id") messageId: Int
    ): Message


    @GET("conversations/{conversation_id}/messages/{message_id}/attachments/{attachment_id}")
    suspend fun getAttachment(
        @Path("conversation_id") conversationId: Int,
        @Path("message_id") messageId: Int,
        @Path("attachment_id") attachmentId: Int
    ): okhttp3.ResponseBody


}
