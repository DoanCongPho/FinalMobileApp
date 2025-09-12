package com.example.finalproject.auth.login.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    val id: Int,
    val name: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    val email: String?,
    @SerializedName("phone_number") val phoneNumber: String?
)
