package com.example.finalproject.journey.model

import com.google.gson.annotations.SerializedName


data class Quiz(
    val id: Int,
    val user_id: Int,
    val title: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    val questions: List<QuizQuestion> = emptyList()
)
