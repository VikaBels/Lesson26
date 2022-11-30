package com.example.lesson26.utils

private const val minuteSeconds = 60
private const val hourSeconds = 3600

fun getFormattedTime(currentTime: Double, timeFormat: String): String {
    val time = currentTime.toInt()

    val milliseconds = getFormattedMilliseconds(time)
    val seconds = getSeconds(time)
    val minutes = getMinutes(time)

    return makeTimeString(minutes, seconds, milliseconds, timeFormat)
}

private fun getFormattedMilliseconds(milliseconds: Int): Int {
    return milliseconds % minuteSeconds
}

private fun getSeconds(milliseconds: Int): Int {
    return milliseconds % hourSeconds / minuteSeconds
}

private fun getMinutes(milliseconds: Int): Int {
    return milliseconds / hourSeconds
}

private fun makeTimeString(hour: Int, min: Int, sec: Int, timeFormat: String): String =
    String.format(timeFormat, hour, min, sec)