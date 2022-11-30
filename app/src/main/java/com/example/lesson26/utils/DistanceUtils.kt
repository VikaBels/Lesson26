package com.example.lesson26.utils

fun getFormattedDistance(distance: Long): String {
    return (distance.toDouble() / 1000).toString()
}