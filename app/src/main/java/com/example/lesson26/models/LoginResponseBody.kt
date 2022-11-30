package com.example.lesson26.models

data class LoginResponseBody(
    val status: String,
    val token: String,
    val firstName: String,
    val lastName: String
)