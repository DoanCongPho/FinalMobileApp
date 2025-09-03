// com/example/finalproject/auth/login/data/LoginRepository.kt
package com.example.finalproject.auth.login.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.runCatching

interface LoginApi {
    // ví dụ sau này:
    // @POST("auth/login")
    // suspend fun login(@Body req: LoginRequest): LoginResponse
}

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)

class LoginRepository(private val api: LoginApi) {
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            runCatching {
                // val resp = api.login(LoginRequest(username, password))
                // resp
                LoginResponse("fake_token") // fake cho demo
            }
        }
    }
}

class LoginViewModelFactory(private val repo: LoginRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(com.example.finalproject.auth.login.viewmodel.LoginViewModel::class.java)) {
            return com.example.finalproject.auth.login.viewmodel.LoginViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

object FakeLoginApi : LoginApi
