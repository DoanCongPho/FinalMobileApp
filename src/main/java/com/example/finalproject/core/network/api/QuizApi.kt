package com.example.finalproject.core.network.api

import com.example.finalproject.journey.model.Quiz
import retrofit2.Response
import retrofit2.http.GET

interface QuizApi {
    @GET("users/me/quizzes")
    suspend fun getUserQuizzes(): Response<List<Quiz>>
}
