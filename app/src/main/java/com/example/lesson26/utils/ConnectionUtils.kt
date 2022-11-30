package com.example.lesson26.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.lesson26.App

fun isOnline(): Boolean {
    val connectivityManager =
        App.getInstanceApp()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val connection =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

    if (connection != null) {
        if (connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            connection.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        ) {
            return true
        }
    }
    return false
}