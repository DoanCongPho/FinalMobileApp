package com.example.finalproject.journey.data

import com.example.finalproject.core.network.api.ApiClient
import com.example.finalproject.journey.model.Quiz
import retrofit2.Response

class QuizRepository {
    private val api = ApiClient.journeyQuizApi

    suspend fun getUserQuizzes(): Response<List<Quiz>> {
        return try {
            api.getUserQuizzes()
        } catch (e: Exception) {
            // Return empty response in case of error
            Response.success(emptyList())
        }
    }
}