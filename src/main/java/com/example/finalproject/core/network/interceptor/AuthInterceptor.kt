package com.example.finalproject.core.network.interceptor

import com.example.finalproject.core.DataStore.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Lấy token đồng bộ (chỉ dùng trong Retrofit interceptor)
        val token = runBlocking { tokenManager.accessToken.firstOrNull() }
        val request = chain.request().newBuilder().apply {
            token?.let { header("Authorization", "Bearer $it") }
        }.build()
        return chain.proceed(request)
    }
}
