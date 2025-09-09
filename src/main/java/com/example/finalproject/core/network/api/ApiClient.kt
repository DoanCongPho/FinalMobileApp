package com.example.finalproject.core.network.api

import com.example.finalproject.core.network.interceptor.AuthInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object ApiClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor({ "token" }))
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://studymate.beerpsi.cc/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()



    val registerApi: RegisterApi = retrofit.create(RegisterApi::class.java)
    val loginApi: LoginApi = retrofit.create(LoginApi::class.java)
}


