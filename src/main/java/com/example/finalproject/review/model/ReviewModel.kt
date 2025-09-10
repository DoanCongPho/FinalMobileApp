package com.example.finalproject.review.model

enum class ReviewItemType { Quiz, Document }

data class RecentQuiz(
    val title: String,
    val progressPercent: Int
)

data class ReviewItem(
    val id: String,
    val title: String,
    val subtitle: String, // “10 quiz”, “Document”, …
    val type: ReviewItemType
)
