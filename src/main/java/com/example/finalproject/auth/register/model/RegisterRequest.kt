package com.example.finalproject.auth.register.model

data class RegisterRequest(
    val email: String,
    val phone_number: String,
    val password: String,
    val name: String
)
