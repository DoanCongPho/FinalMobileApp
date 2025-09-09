package com.example.finalproject.auth.register.model

data class RegisterResponse(
    val id: Int,
    val name: String,
    val created_at: String,
    val updated_at: String,
    val email: String,
    val phone_number: String
)
