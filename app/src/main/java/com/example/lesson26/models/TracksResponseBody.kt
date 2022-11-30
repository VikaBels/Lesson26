package com.example.lesson26.models

data class TracksResponseBody(
    val status: String,
    val tracks: List<Track>
)