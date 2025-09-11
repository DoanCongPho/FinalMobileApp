package com.example.finalproject.core.network.api.chat

import com.example.finalproject.auth.login.model.UserProfile
import com.example.finalproject.chatting.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {

    // Lấy thông tin user theo username
    @GET("users/{username}")
    suspend fun getUserByUsername(
        @Path("username") username: String
    ): User


}