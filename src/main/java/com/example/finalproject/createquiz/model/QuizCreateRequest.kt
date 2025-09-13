package com.example.finalproject.createquiz.model

data class QuizCreateRequest(
    val title: String?,
    val questions: List<QuizQuestionCreateRequest>
)

data class QuizQuestionCreateRequest(
    val question: String,
    val answer: String,
    val explanation: String? = null
)
