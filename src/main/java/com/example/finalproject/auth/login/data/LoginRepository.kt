// com/example/finalproject/auth/login/data/LoginRepository.kt
package com.example.finalproject.auth.login.data
import com.example.finalproject.auth.login.model.LoginResponse
import com.example.finalproject.auth.login.model.UserProfile
import com.example.finalproject.core.network.api.ApiClient
import retrofit2.HttpException
import kotlin.runCatching



class LoginRepository(private val api: ApiClient.ApiClientHolder) {
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return runCatching {
            api.loginApi.login(LoginRequest(username, password))
        }
    }
    suspend fun fetchUserProfile(): Result<UserProfile> {
        return try {
            val response = api.loginApi.getCurrentUser()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


