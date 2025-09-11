package com.example.finalproject.core.network

import com.example.finalproject.core.DataStore.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class TokenProvider(private val tokenManager: TokenManager) {
    fun getToken(): String? {
        return runBlocking {
            try {
                tokenManager.accessToken.first()
            } catch (e: Exception) {
                null
            }
        }
    }
}
