package com.example.finalproject.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            tokenProvider()?.let { token ->
                header("Authorization", "Bearer $token")
            }
        }.build()
        return chain.proceed(request)
    }
}
