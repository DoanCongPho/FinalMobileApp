package com.example.finalproject.createquiz.model

data class ManualQuestion(
    val id: String,
    var question: String = "",
    var answers: MutableList<String> = mutableListOf("",""),
    var correctIndex: Int? = null
)
