package com.example.finalproject.core.network.api.auth
import com.example.finalproject.auth.login.data.LoginRequest
import com.example.finalproject.auth.login.model.LoginResponse
import com.example.finalproject.auth.login.model.UserProfile
import com.example.finalproject.auth.register.model.RegisterRequest
import com.example.finalproject.auth.register.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RegisterApi {
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse
}

interface LoginApi {
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse

    @GET("users/me")
    suspend fun getCurrentUser(): Response<UserProfile>
}


