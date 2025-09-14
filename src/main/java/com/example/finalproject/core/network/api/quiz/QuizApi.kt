package com.example.finalproject.core.network.api.quiz

import com.example.finalproject.createquiz.model.QuizCreateResponse
import com.example.finalproject.createquiz.model.QuizCreateRequest
import com.example.finalproject.journey.model.Quiz
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface QuizApi {
    @GET("users/me/quizzes")
    suspend fun getUserQuizzes(): Response<List<Quiz>>

    @DELETE("quizzes/{quizId}")
    suspend fun deleteQuiz(@Path("quizId") quizId: Int): Response<Unit>

    // Manual Quiz Creation - matches backend.json exactly
    @POST("users/me/quizzes")
    suspend fun createQuiz(@Body request: QuizCreateRequest): Response<Quiz>

    // File-based Quiz Creation - matches backend.json exactly  
    @Multipart
    @POST("users/me/quizzes/from-file")
    suspend fun createQuizFromFile(
        @Part file: MultipartBody.Part,
        @Part("prompt") prompt: RequestBody?,
        @Part("question_count") questionCount: RequestBody
    ): Response<Quiz>

    // Legacy method - keep for backward compatibility if needed
    @Multipart
    @POST("quiz/generate")
    suspend fun generateQuiz(
        @Part files: List<MultipartBody.Part>,
        @Part("prompt") prompt: RequestBody,
        @Part("difficulty") difficulty: RequestBody,
        @Part("num_questions") numQuestions: RequestBody
    ): QuizCreateResponse
}


