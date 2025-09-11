package com.example.finalproject.core.network.api

import com.example.finalproject.core.DataStore.TokenManager
import com.example.finalproject.core.network.api.auth.LoginApi
import com.example.finalproject.core.network.api.auth.RegisterApi
import com.example.finalproject.core.network.api.chat.ConversationApi
import com.example.finalproject.core.network.api.chat.ConversationMessageApi
import com.example.finalproject.core.network.api.chat.UserApi
import com.example.finalproject.core.network.interceptor.AuthInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
object ApiClient {

    fun create(tokenManager: TokenManager): ApiClientHolder {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager)) // ✅ attach token
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://studymate.beerpsi.cc/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return ApiClientHolder(
            retrofit.create(RegisterApi::class.java),
            retrofit.create(LoginApi::class.java),
            retrofit.create(ConversationApi::class.java),
            retrofit.create(UserApi::class.java),
            retrofit.create(ConversationMessageApi::class.java)
        )
    }

    data class ApiClientHolder(
        val registerApi: RegisterApi,
        val loginApi: LoginApi,
        val conversationApi: ConversationApi,
        val userApi: UserApi,
        val messageApi: ConversationMessageApi
    )
}
