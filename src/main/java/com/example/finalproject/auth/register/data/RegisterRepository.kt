package com.example.finalproject.auth.register.data

import com.example.finalproject.auth.register.model.RegisterRequest
import com.example.finalproject.auth.register.model.RegisterResponse
import com.example.finalproject.auth.register.model.RegistrationData
import kotlin.runCatching
import com.example.finalproject.core.network.api.ApiClient

class RegisterRepository {
    private val api = ApiClient.registerApi

    suspend fun register(data: RegistrationData): Result<RegisterResponse> {
        return runCatching {
            val req = RegisterRequest(
                email = data.gmail,
                phone_number = data.phoneNumber,
                password = data.password,
                name = data.fullName
            )
            api.register(req)
        }
    }
}




