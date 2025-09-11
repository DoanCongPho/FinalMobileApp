package com.example.finalproject.createquiz.model // adjust to your package

data class ManualQuestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val question: String = "",
    val answer: String = ""
)
