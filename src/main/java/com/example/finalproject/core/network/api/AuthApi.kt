package com.example.finalproject.core.network.api
import com.example.finalproject.auth.login.data.LoginRequest
import com.example.finalproject.auth.login.model.LoginResponse
import com.example.finalproject.auth.register.model.RegisterRequest
import com.example.finalproject.auth.register.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApi {
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse
}

interface LoginApi {
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse
}


