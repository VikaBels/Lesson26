package com.example.lesson26.utils

import com.example.lesson26.App
import java.util.*

fun getFormattedDate(dataTime: Long): String {
    val netDate = Date(dataTime * 1000)
    return App.getDateFormat().format(netDate)
}

fun refactorDateTime(time: Long): Long {
    return (time - (time % 1000)) / 1000
}