package com.example.finalproject.createquiz.model
data class QuizSource(
    val uriString: String,     // keep as String for easy save/restore
    val displayName: String,
    val mime: String
)
