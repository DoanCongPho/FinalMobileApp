package com.example.finalproject.createquiz.data

import com.example.finalproject.createquiz.model.QuizCreateResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface QuizApi {
    @Multipart
    @POST("quiz/generate")
    suspend fun generateQuiz(
        @Part files: List<MultipartBody.Part>,
        @Part("prompt") prompt: RequestBody,
        @Part("difficulty") difficulty: RequestBody,
        @Part("num_questions") numQuestions: RequestBody
    ): QuizCreateResponse
}
