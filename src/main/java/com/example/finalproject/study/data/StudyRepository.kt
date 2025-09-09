package com.example.finalproject.study.data

import com.example.finalproject.study.model.Quote
import com.example.finalproject.study.model.Topic
import kotlinx.coroutines.delay

class StudyRepository {
    suspend fun loadQuote(): Quote {
        delay(200) // pretend network
        return Quote("Hello May Em", "StudyMate")
    }

    suspend fun loadTopics(): List<Topic> {
        delay(200)
        return listOf(
            Topic("science", "Science", "🧪"),
            Topic("calc3", "Calculus 3", "🧮"),
            Topic("dsa", "DSA", "✨"),
            Topic("eng", "English", "🔤")
        )
    }
}
