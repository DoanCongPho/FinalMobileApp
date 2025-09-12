package com.example.finalproject.chatting.model

import com.google.gson.annotations.SerializedName

data class UpdateConversationRequest(
    val name: String?,
    val description: String?
)

data class User(
    val id: Int,
    val name: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class Participant(
    val user: User,
    val role: String,
    @SerializedName("joined_at") val joinedAt: String
)

data class Conversation(
    val id: Int,
    val type: String,
    val name: String,
    val description: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("require_member_approval") val requireMemberApproval: Boolean,
    val participants: List<Participant>
)



data class CreateConversationRequest(
    val recipient_id: Int? = null,        // direct chat
    val recipient_ids: List<Int>? = null, // group chat
    val name: String? = null,             // group chat
    val description: String? = null,      // group chat
    val type: String                       // "direct" or "group"
)


