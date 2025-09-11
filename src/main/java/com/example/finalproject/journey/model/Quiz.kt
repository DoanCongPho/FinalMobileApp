package com.example.finalproject.journey.model

import java.time.LocalDateTime

data class Quiz(
    val id: Int,
    val user_id: Int,
    val title: String?,
    val created_at: LocalDateTime,
    val updated_at: LocalDateTime,
    val questions: List<QuizQuestion> = emptyList()
)