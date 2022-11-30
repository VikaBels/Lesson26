package com.example.lesson26.models

data class SaveRequestBody(
    val token: String,
    val id: Int? = null,
    val beginsAt: Long,
    val time: Long,
    val distance: Long,
    val points: List<Point>
)