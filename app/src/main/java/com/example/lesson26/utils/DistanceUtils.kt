package com.example.lesson26.utils

import com.example.lesson26.THOUSAND

fun getFormattedDistance(distance: Long): String {
    return (distance.toDouble() / THOUSAND).toString()
}