package com.example.lesson26.utils

import android.database.sqlite.SQLiteException
import com.example.lesson26.R
import java.io.IOException
import java.lang.Exception

fun getIdError(exception: Exception): Int {
    return when (exception) {
        is IOException -> {
            R.string.error_no_internet
        }
        is SQLiteException -> {
            R.string.error_sqlite
        }
        else -> {
            R.string.error_unknown
        }
    }
}