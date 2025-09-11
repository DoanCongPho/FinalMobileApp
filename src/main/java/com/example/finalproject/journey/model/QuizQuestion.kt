package com.example.finalproject.journey.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class QuizQuestion(
    val id: Int,
    val quiz_id: Int,
    val question: String,
    val answer: String,
    val explanation: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
)