// com/example/finalproject/auth/login/data/LoginRepository.kt
package com.example.finalproject.auth.login.data
import com.example.finalproject.auth.login.model.LoginResponse
import com.example.finalproject.core.network.api.ApiClient
import kotlin.runCatching



class LoginRepository {
    private val api = ApiClient.loginApi

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return runCatching {
            api.login(LoginRequest(username, password))
        }
    }
}


