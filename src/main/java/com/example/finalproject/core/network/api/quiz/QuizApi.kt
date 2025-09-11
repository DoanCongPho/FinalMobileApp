package com.example.finalproject.core.network.api.quiz

import com.example.finalproject.createquiz.model.QuizCreateResponse
import com.example.finalproject.journey.model.Quiz
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface QuizApi {
    @GET("users/me/quizzes")
    suspend fun getUserQuizzes(): Response<List<Quiz>>


    @Multipart
    @POST("quiz/generate")
    suspend fun generateQuiz(
        @Part files: List<MultipartBody.Part>,
        @Part("prompt") prompt: RequestBody,
        @Part("difficulty") difficulty: RequestBody,
        @Part("num_questions") numQuestions: RequestBody
    ): QuizCreateResponse

}


