package com.example.finalproject.chatting.data

import retrofit2.HttpException
import com.example.finalproject.auth.login.model.UserProfile
import com.example.finalproject.chatting.model.User
import com.example.finalproject.core.network.api.chat.UserApi


class UserRepository(private val api: UserApi) {

    suspend fun getUserByUsername(username: String): Result<User> {
        return try {
            val res = api.getUserByUsername(username)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
