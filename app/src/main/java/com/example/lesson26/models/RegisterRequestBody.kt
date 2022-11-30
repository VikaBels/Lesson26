package com.example.lesson26.models

data class RegisterRequestBody(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String
)