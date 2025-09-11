package com.example.finalproject.journey.model

import java.time.LocalDateTime

data class QuizQuestion(
    val id: Int,
    val quiz_id: Int,
    val question: String,
    val answer: String,
    val explanation: String?,
    val created_at: LocalDateTime,
    val updated_at: LocalDateTime
)