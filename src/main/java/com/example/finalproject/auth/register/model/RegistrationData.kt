// com.example.finalproject.auth.register.model/RegistrationData.kt
package com.example.finalproject.auth.register.model

data class RegistrationData(
    val studentId: String = "",
    val fullName: String = "",
    val studyField: String = "",
    val gender: Gender? = null,
    val mode: Mode? = null,
    val acceptedPrivacy: Boolean = false
)

enum class Gender { FEMALE, MALE, OTHER }
enum class Mode { STARTER, IMMEDIATE, PRO }
