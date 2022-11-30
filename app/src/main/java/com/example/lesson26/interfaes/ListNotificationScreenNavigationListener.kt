package com.example.lesson26.interfaes

import com.example.lesson26.models.Notification

interface ListNotificationScreenNavigationListener {

    fun showAddNotificationFragment()

    fun showEditNotificationFragment(notification: Notification)
}