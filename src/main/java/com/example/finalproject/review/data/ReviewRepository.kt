package com.example.finalproject.review.data

import com.example.finalproject.review.model.*
import kotlinx.coroutines.delay

interface ReviewRepository {
    suspend fun loadRecent(): RecentQuiz?
    suspend fun loadAvailable(): List<ReviewItem>
    suspend fun deleteItem(id: String) // NEW
}

class FakeReviewRepository : ReviewRepository {
    // in-memory list for demo
    private val memory = mutableListOf(
        ReviewItem("1", "Statistics Quiz", "10 quiz", ReviewItemType.Quiz),
        ReviewItem("2", "Integers Theory", "Document", ReviewItemType.Document)
    )

    override suspend fun loadRecent(): RecentQuiz? {
        delay(150)
        return RecentQuiz(title = "A Basic Music Quiz", progressPercent = 65)
    }

    override suspend fun loadAvailable(): List<ReviewItem> {
        delay(120)
        return memory.toList()
    }

    override suspend fun deleteItem(id: String) {
        delay(80)
        memory.removeAll { it.id == id }
    }
}
