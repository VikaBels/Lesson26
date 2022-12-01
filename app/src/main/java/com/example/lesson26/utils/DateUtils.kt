package com.example.lesson26.utils

import com.example.lesson26.App.Companion.getDateFormat
import com.example.lesson26.THOUSAND
import java.util.*

fun getFormattedDate(dataTime: Long): String {
    val netDate = Date(dataTime * THOUSAND)
    return getDateFormat().format(netDate)
}

fun refactorDateTime(time: Long): Long {
    return (time - (time % THOUSAND)) / THOUSAND
}