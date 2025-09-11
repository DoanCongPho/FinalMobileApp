package com.example.finalproject.core.network.api

import com.example.finalproject.core.network.interceptor.AuthInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ApiClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Custom Gson with LocalDateTime support
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer<LocalDateTime> { json, _, _ ->
            LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        })
        .create()

    private var tokenProvider: (() -> String?) = { "token" }
    
    fun setTokenProvider(provider: () -> String?) {
        tokenProvider = provider
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor { tokenProvider() })
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://studymate.beerpsi.cc/api/v1/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    val registerApi: RegisterApi = retrofit.create(RegisterApi::class.java)
    val loginApi: LoginApi = retrofit.create(LoginApi::class.java)
    val journeyQuizApi: QuizApi = retrofit.create(QuizApi::class.java)

    val quizApi: com.example.finalproject.createquiz.data.QuizApi =
        retrofit.create(com.example.finalproject.createquiz.data.QuizApi::class.java)

}




