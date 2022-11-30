package com.example.lesson26.interfaes

import com.example.lesson26.models.Notification

interface NotificationListener {

    fun onNotificationClick(notification: Notification)

    fun onBtnDeleteClick(notification: Notification)
}