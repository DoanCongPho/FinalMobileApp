package com.example.finalproject.chatting.model


import com.google.gson.annotations.SerializedName
import java.io.File

data class Attachment(
    val id: Int,
    val filename: String,
    val content_type: String,
    val file_size: Int,
    val url: String? = null
)

data class Message(
    val id: Int,
    @SerializedName("conversation_id") val conversationId: Int,
    @SerializedName("reply_to_id") val replyToId: Int?,
    @SerializedName("user_id") val userId: Int,
    val content: String?,   // nullable vì server cho phép null
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("edited_at") val editedAt: String?,
    @SerializedName("attachments") val attachments: List<Attachment> = emptyList()
)


data class CreateMessageRequest(
    val content: String,
    val reply_to_id: Int? = null,
    val attachments: List<File>? = null
)
