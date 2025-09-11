package com.example.finalproject.chatting.data

import android.util.Log
import com.example.finalproject.chatting.model.Message
import com.example.finalproject.chatting.model.CreateMessageRequest
import com.example.finalproject.core.network.api.chat.ConversationMessageApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


fun String.toRequestBody(): RequestBody =
    RequestBody.create("text/plain".toMediaTypeOrNull(), this)

fun File.toMultipartBody(name: String): MultipartBody.Part {
    val requestFile = this.asRequestBody("application/octet-stream".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(name, this.name, requestFile)
}


class MessageRepository(private val api: ConversationMessageApi) {

    suspend fun getMessages(conversationId: Int): Result<List<Message>> {
        return try {
            val response = api.getMessages(conversationId)
            Log.d("API", "Raw response: ${response.raw()}")
            Log.d("API", "Body: ${response.body()}")
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("HTTP ${response.code()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Log.e("API", "Error getting messages", e)
            Result.failure(e)
        }
    }





    suspend fun sendMessage(
        conversationId: Int,
        content: String,
        replyToId: Int? = null,
        attachments: List<File>? = null
    ): Result<Message> {
        return try {
            val contentPart = content.toRequestBody()
            val replyPart = replyToId?.toString()?.toRequestBody()
            val attachmentParts = attachments?.map { it.toMultipartBody("attachments") }

            val res = api.sendMessage(
                conversationId = conversationId,
                content = contentPart,
                replyToId = replyPart,
                attachments = attachmentParts
            )
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun deleteMessage(conversationId: Int, messageId: Int): Result<Message> {
        return try {
            val res = api.deleteMessage(conversationId, messageId)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
